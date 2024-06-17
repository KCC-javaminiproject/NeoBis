package deposit;

import java.util.ArrayList;
import java.util.List;

public class AntiMoneyLaundering {
	private long limit;
	private List<Customer> criticalCustomer;

	public AntiMoneyLaundering() {
		this.limit = 10000000;
		criticalCustomer = new ArrayList<>();
	}

	public boolean customerInformationCheck(Customer customer) {
		if (watchListFiltering(customer)) { // ���͸��� �ɷ����� ���� �˻� ����
			if (suspiciousTransactionReport(customer)) {
				if (currencyTransactionReport(customer)) {

				}
			}
		}
		return false;
	}

	public boolean watchListFiltering(Customer customer) {
		for(Customer c : criticalCustomer) {
			if(c.getName().equals(customer.getName()) && (c.getRegistrationNumber() == customer.getRegistrationNumber())){
				return true;
			}
		}
		return false;
	}

	public boolean suspiciousTransactionReport(Customer customer) {
        System.out.println("�ǽ� �ŷ� �߰�");
		return true;
	}

	public boolean currencyTransactionReport(Customer customer) {

		return true;
	}

	public boolean customerDueDiligence(Customer customer) {
		// �ſ� Ȯ��
        if (customer.getName() == null || customer.getName().isEmpty()) {
            System.out.println("�̸��� ��� �ֽ��ϴ�.");
            return false;
        }
        if (customer.getPhone() == null || customer.getPhone().isEmpty()) {
            System.out.println("��ȭ��ȣ�� ��� �ֽ��ϴ�.");
            return false;
        }
        if (customer.getAddress() == null || customer.getAddress().isEmpty()) {
            System.out.println("�ּҰ� ��� �ֽ��ϴ�.");
            return false;
        }
        if (customer.getRegistrationNumber() == 0) {
            System.out.println("��� ��ȣ�� ��ȿ���� �ʽ��ϴ�.");
            return false;
        }

        // ���� ��
        if (watchListFiltering(customer)) {
            System.out.println("���� ������ ��Ͽ� �ֽ��ϴ�.");
            return false;
        }

        // �ŷ� ����͸�
        if (!suspiciousTransactionReport(customer)) {
            System.out.println("�ǽ� �ŷ��� �߰ߵ��� �ʾҽ��ϴ�.");
            return true;
        }

        return true;
	}
}
