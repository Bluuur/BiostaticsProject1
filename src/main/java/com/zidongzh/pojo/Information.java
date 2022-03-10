package com.zidongzh.pojo;

/**
 * @author ZidongZh
 * @date 2022/3/3
 */
public class Information {
    private String chromosomeId;
    private String type;
    private Integer startPos;
    private Integer endPos;
    private Integer mutationNum;
    private Double mutationRate;
    private Integer AGNum;
    private Integer ACNum;
    private Integer ATNum;
    private Integer GANum;
    private Integer GCNum;
    private Integer GTNum;
    private Integer CANum;
    private Integer CGNum;
    private Integer CTNum;
    private Integer TANum;
    private Integer TGNum;
    private Integer TCNum;

    public Information() {
    }

    public Information(String chromosomeId, String type, Integer startPos, Integer endPos) {
        this.chromosomeId = chromosomeId;
        this.type = type;
        this.startPos = startPos;
        this.endPos = endPos;
        this.mutationNum = 0;
        this.mutationRate = 0.0;
        this.ACNum = 0;
        this.AGNum = 0;
        this.ATNum = 0;
        this.GANum = 0;
        this.GCNum = 0;
        this.GTNum = 0;
        this.CANum = 0;
        this.CGNum = 0;
        this.CTNum = 0;
        this.TANum = 0;
        this.TCNum = 0;
        this.TGNum = 0;
    }

    public Information(String chromosomeId, String type, Integer startPos, Integer endPos, Integer mutationNum, Double mutationRate, Integer AGNum, Integer ACNum, Integer ATNum, Integer GANum, Integer GCNum, Integer GTNum, Integer CANum, Integer CGNum, Integer CTNum, Integer TANum, Integer TGNum, Integer TCNum) {
        this.chromosomeId = chromosomeId;
        this.type = type;
        this.startPos = startPos;
        this.endPos = endPos;
        this.mutationNum = mutationNum;
        this.mutationRate = mutationRate;
        this.AGNum = AGNum;
        this.ACNum = ACNum;
        this.ATNum = ATNum;
        this.GANum = GANum;
        this.GCNum = GCNum;
        this.GTNum = GTNum;
        this.CANum = CANum;
        this.CGNum = CGNum;
        this.CTNum = CTNum;
        this.TANum = TANum;
        this.TGNum = TGNum;
        this.TCNum = TCNum;
    }

    @Override
    public String toString() {
        return "Information{" +
                "chromosomeId='" + chromosomeId + '\'' +
                ", type='" + type + '\'' +
                ", startPos=" + startPos +
                ", endPos=" + endPos +
                ", mutationNum=" + mutationNum +
                ", mutationRate=" + mutationRate +
                ", AGNum=" + AGNum +
                ", ACNum=" + ACNum +
                ", ATNum=" + ATNum +
                ", GANum=" + GANum +
                ", GCNum=" + GCNum +
                ", GTNum=" + GTNum +
                ", CANum=" + CANum +
                ", CGNum=" + CGNum +
                ", CTNum=" + CTNum +
                ", TANum=" + TANum +
                ", TGNum=" + TGNum +
                ", TCNum=" + TCNum +
                '}';
    }

    public String getChromosomeId() {
        return chromosomeId;
    }

    public void setChromosomeId(String chromosomeId) {
        this.chromosomeId = chromosomeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStartPos() {
        return startPos;
    }

    public void setStartPos(Integer startPos) {
        this.startPos = startPos;
    }

    public Integer getEndPos() {
        return endPos;
    }

    public void setEndPos(Integer endPos) {
        this.endPos = endPos;
    }

    public Integer getMutationNum() {
        return mutationNum;
    }

    public void setMutationNum(Integer mutationNum) {
        this.mutationNum = mutationNum;
    }

    public Double getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(Double mutationRate) {
        this.mutationRate = mutationRate;
    }

    public Integer getAGNum() {
        return AGNum;
    }

    public void setAGNum(Integer AGNum) {
        this.AGNum = AGNum;
    }

    public Integer getACNum() {
        return ACNum;
    }

    public void setACNum(Integer ACNum) {
        this.ACNum = ACNum;
    }

    public Integer getATNum() {
        return ATNum;
    }

    public void setATNum(Integer ATNum) {
        this.ATNum = ATNum;
    }

    public Integer getGANum() {
        return GANum;
    }

    public void setGANum(Integer GANum) {
        this.GANum = GANum;
    }

    public Integer getGCNum() {
        return GCNum;
    }

    public void setGCNum(Integer GCNum) {
        this.GCNum = GCNum;
    }

    public Integer getGTNum() {
        return GTNum;
    }

    public void setGTNum(Integer GTNum) {
        this.GTNum = GTNum;
    }

    public Integer getCANum() {
        return CANum;
    }

    public void setCANum(Integer CANum) {
        this.CANum = CANum;
    }

    public Integer getCGNum() {
        return CGNum;
    }

    public void setCGNum(Integer CGNum) {
        this.CGNum = CGNum;
    }

    public Integer getCTNum() {
        return CTNum;
    }

    public void setCTNum(Integer CTNum) {
        this.CTNum = CTNum;
    }

    public Integer getTANum() {
        return TANum;
    }

    public void setTANum(Integer TANum) {
        this.TANum = TANum;
    }

    public Integer getTGNum() {
        return TGNum;
    }

    public void setTGNum(Integer TGNum) {
        this.TGNum = TGNum;
    }

    public Integer getTCNum() {
        return TCNum;
    }

    public void setTCNum(Integer TCNum) {
        this.TCNum = TCNum;
    }
}
