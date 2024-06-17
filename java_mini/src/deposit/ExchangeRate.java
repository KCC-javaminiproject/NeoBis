package deposit;

public class ExchangeRate {
	private static Map<Country, Double> exchanges;
	
	static  {
		exchanges.put(Country.USA, 1200.0);
		exchanges.put(Country.CHI, 1200.0);
		exchanges.put(Country.JAP, 1200.0);
		exchanges.put(Country.KOR, 1200.0);
		exchanges.put(Country.UK, 1200.0);
	}
	
	public void printExchanges() {
		int idx = 1;
		for(Map.Entry<Country, Double> entry : exchanges.entrySet()) {
			System.out.println(idx + "��° ���� �̸��� : " + entry.getKey());
			System.out.println("���� ȯ�� : " + entry.getValue());
			idx++;
		}
	} 
	
	 // ȯ�� ��� �޼ҵ�
    public static long calExchange(Country country, long exchangeAmount) {
        Double rate = exchanges.get(country);
        if (rate == null) {
            throw new IllegalArgumentException("ȯ�� ������ �����ϴ�: " + country);
        }
        return Math.round(exchangeAmount * rate); // ȯ���� ���� �ݾ� ��ȯ
    }
 
}
