package kosa.banksystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AntiMoneyLaundering {
	private long limit;
	private List<Customer> criticalCustomer;

	public AntiMoneyLaundering() {
		this.limit = 10000000;
		criticalCustomer = new ArrayList<>();
	}

	// KYC
	public boolean knowYourCustomer(Customer customer, long amount) {
		if (!customer.isCdd() || !checkExpiredCdd(customer)) {
			try {
				if (!customerDueDiligence(customer)) {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (watchListFiltering(customer)) { // 필터링에 걸렸으면 거래 불가
			return false;
		}
		if (suspiciousTransactionReport(customer, amount)) { // 의심 거래 식별 보고
			return false;
		} else if (currencyTransactionReport(amount)) {// 일정 금액 이상 현금 거래 보고
			try {
				if (!customerDueDiligence(customer)) {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	// WLF
	public boolean watchListFiltering(Customer customer) {
		for (Customer c : criticalCustomer) {
			if (c.getName().equals(customer.getName())
					&& (c.getRegistrationNumber().equals(customer.getRegistrationNumber()))) {
				return true;
			}
		}
		System.out.println("WLF : 고위험 고객이 아닙니다");
		return false;
	}

	// STR
	public boolean suspiciousTransactionReport(Customer customer, long amount) {
		// 직업이 없거나, 개좌의 갯수가 2개 이상이거나, 천 만원 이상 거래하는 경우
		if (customer.getJob() == null && customer.getAccounts().size() > 2) {
			System.out.println("의심 거래 발견!! 금융정보분석원으로 보고가 완료되었습니다.");
			return true;
		}
		// 고객이 미성년자이면서 천 만원 이상 거래하는 경우
		else if (isTeenage(customer.getRegistrationNumber()) && amount >= limit) {
			System.out.println("미성년자 고액 거래 발견!! 금융정보분석원으로 보고가 완료되었습니다.");
			return true;
		}
		// 위험 고객이 아니거나, 계좌의 실 소유자가 아닌 경우
		else if (customer.isOwner() == false) {
			System.out.println("대포 통장 거래 의심 발견!! 금융정보분석원으로 보고가 완료되었습니다.");
			return true;
		}
		// 하루 동안 분할거래가 3회 이상인 경우
		else if (getTradesForToday(customer) >= 3) {
			System.out.println("의심 거래 패턴 발견!! 금융정보분석원으로 보고가 완료되었습니다.");
			return true;
		}
		System.out.println("STR : 의심 거래가 발견되지 않았습니다.");
		return false;
	}

	// CTR
	public boolean currencyTransactionReport(long amonut) {
		if (amonut >= limit) {
			System.out.println("원화 1천만원 이상 거래 발견!! 금융정보분석원으로 보고가 완료되었습니다.");
			return true;
		}
		System.out.println("CTR : 고액 현금 거래가 발견되지 않았습니다.");
		return false;
	}

	// CDD
	public boolean customerDueDiligence(Customer customer) throws IOException {
		System.out.println("CDD : 고객확인제도를 위해 개인정보를 확인합니다. ");
		BufferedReader br = DataInput.getInstance();

		System.out.print("이름을 입력하세요 : ");
		String name = br.readLine();
		if (!(customer.getName().equals(name))) {
			System.out.println("이름이 일치하지 않습니다.");
			return false;
		}
		System.out.print("휴대폰 번호를 입력하세요 : ");
		String phone = br.readLine();
		if (!(customer.getPhone().equals(phone))) {
			System.out.println("휴대폰 번호가 일치하지 않습니다.");
			return false;
		}
		System.out.print("주민등록번호를 입력하세요 : ");
		String registrationNumber = br.readLine();
		if (!(customer.getRegistrationNumber().equals(registrationNumber))) {
			System.out.println("주민등록번호가 일치하지 않습니다.");
			return false;
		}
		System.out.print("주소를 입력하세요.(ex.서울시 종로구 혜화동) : ");
		String address = br.readLine();
		if (!(customer.getAddress().equals(address))) {
			System.out.println("주소가 일치하지 않습니다.");
			return false;
		}
		System.out.print("직업을 입력하세요 : ");
		String job = br.readLine();
		if (!(customer.getJob().equals(job))) {
			System.out.println("직업이 일치하지 않습니다.");
			return false;
		}
		System.out.println("거래하려는 계좌의 실소유자가 맞습니까?");
		System.out.println("1. 예\t2.아니요");
		int num = Integer.parseInt(br.readLine());
		if (num == 2) {
			System.out.println("계좌의 실소유주가 아닙니다");
			customer.setOwner(false);
			return false;
		}

		customer.setCdd(true);
		customer.setCddDate(LocalDateTime.now());
		// 거래 모니터링
		System.out.println("고객 확인이 완료되었습니다.");
		return true;
	}

	// 미성년자 여부 체크
	public boolean isTeenage(String customerAge) {
		int birthYear = Integer.parseInt(customerAge.substring(0, 2));

		// 현재 연도 가져오기
		int currentYear = java.time.Year.now().getValue();
		int age;

		if (birthYear <= 24) {
			// 2000년대 출생자
			age = currentYear - (2000 + birthYear);
			// 2024 - (2000 + 05) =
		} else {
			// 1900년대 출생자
			age = currentYear - (1900 + birthYear);
		}
		if (age < 20) {
			return true;
		}
		return false;

	}

	// 일일 거래 3회 이상 감지
	public int getTradesForToday(Customer customer) {
		LocalDate today = LocalDate.now();
		List<Trade> trades = customer.getAccounts().get(0).getTrades().stream()
				.filter(trade -> trade.getTime().toLocalDate().equals(today)).collect(Collectors.toList());
		return trades.size();
	}

	// 고객 확인 만기 여부 체크
	public boolean checkExpiredCdd(Customer customer) {
		LocalDateTime cddDate = customer.getCddDate();
		if (cddDate != null) {
			LocalDateTime expiryDate = cddDate.plusYears(3);
			LocalDateTime currentDate = LocalDateTime.now();

			if (currentDate.isAfter(expiryDate)) {
				return false;
			}
		}
		return true;
	}
}