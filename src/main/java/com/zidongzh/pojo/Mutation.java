package com.zidongzh.pojo;

/**
 * @author ZidongZh
 * @date 2022/3/3
 */
public class Mutation {
    private String chromosomeId;
    private Integer chromosomePos;
    private String type;

    /**
     * constructor
     *
     * @param chromosomeId  染色体号
     * @param chromosomePos 染色体位置
     * @param type          变异类型
     */
    public Mutation(
            String chromosomeId,
            Integer chromosomePos,
            String type) {
        this.chromosomeId = chromosomeId;
        this.chromosomePos = chromosomePos;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Mutation{" +
                "chromosomeId='" + chromosomeId + '\'' +
                ", chromosomePos=" + chromosomePos +
                ", type='" + type + '\'' +
                '}';
    }

    // getter and setter

    public String getChromosomeId() {
        return chromosomeId;
    }

    public void setChromosomeId(String chromosomeId) {
        this.chromosomeId = chromosomeId;
    }

    public Integer getChromosomePos() {
        return chromosomePos;
    }

    public void setChromosomePos(Integer chromosomePos) {
        this.chromosomePos = chromosomePos;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
