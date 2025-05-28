package kr.kh.boot.model.form;

public class UserAssetForm {
    private long cashWon;
    private long cashDollar;
    private long deposits;
    private long bond;
    private long gold;
    private long etf;

    // 기본 생성자
    public UserAssetForm() {}

    // Getter/Setter
    public long getCashWon() { return cashWon; }
    public void setCashWon(long cashWon) { this.cashWon = cashWon; }

    public long getCashDollar() { return cashDollar; }
    public void setCashDollar(long cashDollar) { this.cashDollar = cashDollar; }

    public long getDeposits() { return deposits; }
    public void setDeposits(long deposits) { this.deposits = deposits; }

    public long getBond() { return bond; }
    public void setBond(long bond) { this.bond = bond; }

    public long getGold() { return gold; }
    public void setGold(long gold) { this.gold = gold; }

    public long getEtf() { return etf; }
    public void setEtf(long etf) { this.etf = etf; }

}
