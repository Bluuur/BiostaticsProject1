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

    /**
     * constructor
     *
     * @param chromosomeId 染色体号
     * @param type         类型 (基因，非基因)
     * @param startPos     起始位置
     * @param endPos       终止位置
     * @param mutationNum  变异数
     */
    public Information(
            String chromosomeId,
            String type,
            Integer startPos,
            Integer endPos,
            Integer mutationNum,
            Double mutationRate ) {
        this.chromosomeId = chromosomeId;
        this.type = type;
        this.startPos = startPos;
        this.endPos = endPos;
        this.mutationNum = mutationNum;
        this.mutationRate = mutationRate;
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
                '}';
    }

    // getter and setter


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
}
