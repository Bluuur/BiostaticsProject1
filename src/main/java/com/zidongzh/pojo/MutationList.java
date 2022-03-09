package com.zidongzh.pojo;

/**
 * @author Zidong Zh
 * @date 2022/3/9
 */
public class MutationList {
    private String type;
    private Integer len;
    private Integer num;
    private Boolean isGene;

    public MutationList() {
    }

    public MutationList(String type, Integer len, Integer num, Boolean isGene) {
        this.type = type;
        this.len = len;
        this.num = num;
        this.isGene = isGene;
    }

    @Override
    public String toString() {
        return "MutationList{" +
                "type='" + type + '\'' +
                ", len=" + len +
                ", num=" + num +
                ", isGene=" + isGene +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Boolean getGene() {
        return isGene;
    }

    public void setGene(Boolean gene) {
        isGene = gene;
    }
}


