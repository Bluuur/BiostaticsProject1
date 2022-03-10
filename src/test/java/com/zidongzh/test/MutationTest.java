package com.zidongzh.test;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.zidongzh.mapper.InformationMapper;
import com.zidongzh.mapper.MutationMapper;
import com.zidongzh.pojo.Information;
import com.zidongzh.pojo.Mutation;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.*;

/**
 * @author Zidong Zh
 * @date 2022/3/3
 */
public class MutationTest {

    @Test
    public void myTest() throws IOException {
        List<Mutation> allMutation = null;
        List<Information> allInformation = null;

        List<Information> genes = null;
        List<Information> nonGenes = new ArrayList<>();
        List<Information> chromosomes = null;

        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //获取Mapper接口
        MutationMapper mutationMapper = sqlSession.getMapper(MutationMapper.class);
        InformationMapper informationMapper = sqlSession.getMapper(InformationMapper.class);

        //获取数据
        allMutation = mutationMapper.getAll();
        System.out.println("Mutation size = " + allMutation.size());

        //获取基因区间
        genes = informationMapper.getInfoByType("gene");
        System.out.println("genes size is " + genes.size());

        //获取染色体
        chromosomes = informationMapper.getInfoByType("chromosome");

        //合并重叠基因区间
        connect(genes);

        //如果是染色体的最后一个基因
        //那么最后这个基因到该染色体末尾之间的区域也是基因间区
        for (int i = 0; i < chromosomes.size(); i++) {
            for (int j = 0; j + 1 < genes.size(); j++) {
                if (genes.get(j).getChromosomeId().equals(chromosomes.get(i).getChromosomeId()) &&
                        !genes.get(j + 1).getChromosomeId().equals(chromosomes.get(i).getChromosomeId())) {
                    genes.get(j).setEndPos(chromosomes.get(i).getEndPos());
                }
            }
        }

        //构造非基因区间
        nonGenes = getNonGenes(genes, chromosomes);
        //生成数据漏掉三条
        nonGenes.add(new Information("2-micron", "nonGene", 1524, 1886));
        nonGenes.add(new Information("2-micron", "nonGene", 3009, 3270));
        nonGenes.add(new Information("2-micron", "nonGene", 3817, 5307));

        System.out.println("nonGene size = " + nonGenes.size());

        //计算变异数与变异率
        for (int i = 0; i < allMutation.size(); i++) {
            for (int j = 0; j < genes.size(); j++) {
                if (allMutation.get(i).getChromosomeId().equals(genes.get(j).getChromosomeId()) && allMutation.get(i).getChromosomePos() >= genes.get(j).getStartPos() &&
                        allMutation.get(i).getChromosomePos() <= genes.get(j).getEndPos()) {
                    genes.get(j).setMutationNum(genes.get(j).getMutationNum() + 1);
                    genes.get(j).setMutationRate((double) genes.get(j).getMutationNum() / (double) (genes.get(j).getEndPos() - genes.get(j).getStartPos() + 1));
                }
            }
            for (int j = 0; j < nonGenes.size(); j++) {
                if (allMutation.get(i).getChromosomeId().equals(nonGenes.get(j).getChromosomeId()) && allMutation.get(i).getChromosomePos() >= nonGenes.get(j).getStartPos() &&
                        allMutation.get(i).getChromosomePos() <= nonGenes.get(j).getEndPos()) {
                    nonGenes.get(j).setMutationNum(nonGenes.get(j).getMutationNum() + 1);
                    nonGenes.get(j).setMutationRate((double) nonGenes.get(j).getMutationNum() / (double) (nonGenes.get(j).getEndPos() - nonGenes.get(j).getStartPos() + 1));
                }
            }
        }

        //统计突变方向
        for (int i = 0; i < genes.size(); i++) {
            Information gene = genes.get(i);
            for (int j = 0; j < allMutation.size(); j++) {
                Mutation mutation = allMutation.get(j);
                classify(gene, mutation);
            }
        }
        for (int i = 0; i < nonGenes.size(); i++) {
            Information nonGene = nonGenes.get(i);
            for (int j = 0; j < allMutation.size(); j++) {
                Mutation mutation = allMutation.get(j);
                classify(nonGene, mutation);
            }
        }

        //将基因与非基因添加到数据库
        for (int i = 0; i < genes.size(); i++) {
            informationMapper.addGeneInfo(genes.get(i));
        }
        for (int i = 0; i < nonGenes.size(); i++) {
            informationMapper.addNonGeneInfo(nonGenes.get(i));
        }

        //提交修改至数据库
        sqlSession.commit();
    }

    @Test
    public void bioTest() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        InformationMapper informationMapper = sqlSession.getMapper(InformationMapper.class);

        List<Information> genes = informationMapper.getGenes();
        List<Information> nonGenes = informationMapper.getNonGenes();

        double geneMutRateMean = 0.0;
        double geneVariance = 0.0;

        double nonGeneMutRateMean = 0.0;
        double nonGeneVariance = 0.0;

        geneMutRateMean = getMean(genes);
        geneVariance = getVariance(genes, geneMutRateMean);
        System.out.println("geneMutRateMean = " + geneMutRateMean);
        System.out.println("geneVariance  = " + geneVariance);

        nonGeneMutRateMean = getMean(nonGenes);
        nonGeneVariance = getVariance(nonGenes, nonGeneMutRateMean);
        System.out.println("nonGeneMutRateMean = " + nonGeneMutRateMean);
        System.out.println("nonGeneVariance = " + nonGeneVariance);

        //t检验
        double t = 0.0;
        t = (geneMutRateMean - nonGeneMutRateMean) / (sqrt((((genes.size() - 1) * geneVariance) + ((nonGenes.size() - 1) * nonGeneVariance)) / (genes.size() + nonGenes.size() - 2)) * sqrt((1 / (double) genes.size()) + (1 / (double) nonGenes.size())));
        System.out.println("t = " + t);
        //u 检验
        double u = 0.0;
        u = (geneMutRateMean - nonGeneMutRateMean) / (sqrt((geneVariance / genes.size()) + (nonGeneVariance / nonGenes.size())));
        System.out.println("u = " + u);
    }

    @Test
    public void mutationTest() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        InformationMapper informationMapper = sqlSession.getMapper(InformationMapper.class);
        MutationMapper mutationMapper = sqlSession.getMapper(MutationMapper.class);

        List<Information> genes = informationMapper.getGenes();
        List<Information> nonGenes = informationMapper.getNonGenes();

        System.out.println("------------------------------------");
        System.out.println("AC: t is " + tTest(genes, nonGenes, "AC"));
        System.out.println("------------------------------------");
        System.out.println("AT: t is " + tTest(genes, nonGenes, "AT"));
        System.out.println("------------------------------------");
        System.out.println("GA: t is " + tTest(genes, nonGenes, "GA"));
        System.out.println("------------------------------------");
        System.out.println("GC: t is " + tTest(genes, nonGenes, "GC"));
        System.out.println("------------------------------------");
        System.out.println("GT: t is " + tTest(genes, nonGenes, "GT"));
        System.out.println("------------------------------------");
        System.out.println("CA: t is " + tTest(genes, nonGenes, "CA"));
        System.out.println("------------------------------------");
        System.out.println("CG: t is " + tTest(genes, nonGenes, "CG"));
        System.out.println("------------------------------------");
        System.out.println("CT: t is " + tTest(genes, nonGenes, "CT"));
        System.out.println("------------------------------------");
        System.out.println("TA: t is " + tTest(genes, nonGenes, "TA"));
        System.out.println();
        System.out.println();
        System.out.println("------------------------------------");
        System.out.println("AC: u is " + uTest(genes, nonGenes, "AC"));
        System.out.println("-----------------------------------");
        System.out.println("AT: u is " + uTest(genes, nonGenes, "AT"));
        System.out.println("-----------------------------------");
        System.out.println("GA: u is " + uTest(genes, nonGenes, "GA"));
        System.out.println("-----------------------------------");
        System.out.println("GC: u is " + uTest(genes, nonGenes, "GC"));
        System.out.println("-----------------------------------");
        System.out.println("GT: u is " + uTest(genes, nonGenes, "GT"));
        System.out.println("-----------------------------------");
        System.out.println("CA: u is " + uTest(genes, nonGenes, "CA"));
        System.out.println("-----------------------------------");
        System.out.println("CG: u is " + uTest(genes, nonGenes, "CG"));
        System.out.println("----------------------------------");
        System.out.println("CT: u is " + uTest(genes, nonGenes, "CT"));
        System.out.println("-----------------------------------");
        System.out.println("TA: u is " + uTest(genes, nonGenes, "TA"));
    }


    void classify(Information gene, Mutation mutation) {
        if (mutation.getChromosomeId().equals(gene.getChromosomeId()) &&
                mutation.getChromosomePos() >= gene.getStartPos() &&
                mutation.getChromosomePos() <= gene.getEndPos()) {
            if (mutation.getType().startsWith("AT")) {
                gene.setATNum(gene.getATNum() + 1);
            } else if (mutation.getType().startsWith("AG")) {
                gene.setAGNum(gene.getAGNum() + 1);
            } else if (mutation.getType().startsWith("AC")) {
                gene.setACNum(gene.getACNum() + 1);
            } else if (mutation.getType().startsWith("TA")) {
                gene.setTANum(gene.getTANum() + 1);
            } else if (mutation.getType().startsWith("TG")) {
                gene.setTGNum(gene.getTGNum() + 1);
            } else if (mutation.getType().startsWith("TC")) {
                gene.setTCNum(gene.getTCNum() + 1);
            } else if (mutation.getType().startsWith("GA")) {
                gene.setGANum(gene.getGANum() + 1);
            } else if (mutation.getType().startsWith("GT")) {
                gene.setGTNum(gene.getGTNum() + 1);
            } else if (mutation.getType().startsWith("GC")) {
                gene.setGCNum(gene.getGCNum() + 1);
            } else if (mutation.getType().startsWith("CA")) {
                gene.setCANum(gene.getCANum() + 1);
            } else if (mutation.getType().startsWith("CT")) {
                gene.setCTNum(gene.getCTNum() + 1);
            } else if (mutation.getType().startsWith("CG")) {
                gene.setCGNum(gene.getCGNum() + 1);
            }
        }
    }

    /**
     * 合并重叠基因区间
     *
     * @param information 原始数据
     */
    void connect(List<Information> information) {
        int i = 0;
        int j = 1;
        Information previous = null;
        Information next = null;
        while (j + 1 < information.size()) {
            previous = information.get(i);
            next = information.get(j);
            if (previous.getEndPos() >= next.getStartPos() && previous.getEndPos() <= next.getEndPos()) {
                previous.setEndPos(next.getEndPos());
                information.remove(j);
                information.set(i, previous);
            } else {
                i++;
                j++;
            }
        }
    }


    /**
     * @param genes 基因信息
     * @return 非基因区间
     */
    List<Information> getNonGenes(List<Information> genes, List<Information> chromosomes) {
        int pos = 0;

        List<Information> nonGenes = new ArrayList<>();
        Information nonGeneHead = new Information(chromosomes.get(0).getChromosomeId(), "nonGene", 1, genes.get(0).getStartPos() - 1);
        nonGenes.add(nonGeneHead);
        for (int i = 0; i < chromosomes.size(); i++) {
            for (int j = 0; j < genes.size(); j++) {
                if (genes.get(j).getChromosomeId().equals(chromosomes.get(i).getChromosomeId()) &&
                        genes.get(j + 1).getChromosomeId().equals(chromosomes.get(i).getChromosomeId())) {
                    Information nonGene = new Information(genes.get(j).getChromosomeId(), "nonGene", genes.get(j).getEndPos() + 1, genes.get(j + 1).getStartPos() - 1);
                    nonGenes.add(nonGene);
                    pos = j + 2;
                }
            }
            if (null != genes.get(pos)) {
                Information nonGeneHead1 = new Information(genes.get(pos).getChromosomeId(), "nonGene", 1, genes.get(pos).getStartPos() - 1);
                nonGenes.add(nonGeneHead1);
            }
        }
        return nonGenes;
    }

    /**
     * 计算变异率均值
     *
     * @param information 传入的基因（非基因）集合
     * @return 返回变异率均值
     */
    double getMean(List<Information> information) {
        double sum = 0.0;
        for (int i = 0; i < information.size(); i++) {
            sum += information.get(i).getMutationRate();
        }
        return sum / information.size();
    }

    /**
     * 计算方差
     *
     * @param information 传入的基因（非基因）集合
     * @param mean        变异率均值
     * @return 返回方差
     */
    double getVariance(List<Information> information, double mean) {
        double sum = 0.0;
        for (int i = 0; i < information.size(); i++) {
            sum += pow((information.get(i).getMutationRate() - mean), (double) 2);
        }
        return sum / (information.size() - 1);
    }

    /**
     * t检验
     *
     * @param genes    基因序列数据
     * @param nonGenes 非基因序列数据
     * @param flag     需要计算的突变类型 “AG” 表示由 A 突变为 G
     * @return
     */
    double tTest(List<Information> genes, List<Information> nonGenes, String flag) {

        List<Double> geneRate = rate(genes, flag);
        List<Double> nonGeneRate = rate(nonGenes, flag);
        double geneMutRateMean = 0.0;
        double nonGeneMutRateMean = 0.0;
        double geneSum = 0.0;
        double nonGeneSum = 0.0;
        double geneVariance = 0.0;
        double nonGeneVariance = 0.0;
        double t = 0.0;

        geneMutRateMean = mean(geneRate);
        nonGeneMutRateMean = mean(nonGeneRate);
        geneSum = (double) genes.size();
        nonGeneSum = (double) nonGenes.size();
        geneVariance = variance(geneRate, geneMutRateMean);
        nonGeneVariance = variance(nonGeneRate, nonGeneMutRateMean);

        t = (geneMutRateMean - nonGeneMutRateMean) / (sqrt((((geneSum - 1) * geneVariance) + ((nonGeneSum - 1) * nonGeneVariance)) / (geneSum + nonGeneSum - 2)) * sqrt((1 / geneSum) + (1 / nonGeneSum)));
        System.out.println("gene mean is " + geneMutRateMean);
        System.out.println("non gene mean is " + nonGeneMutRateMean);
        return t;
    }

    /**
     * u检验
     *
     * @param genes    基因序列数据
     * @param nonGenes 非基因序列数据
     * @param flag     需要计算的突变类型 “AG” 表示由 A 突变为 G
     * @return
     */
    double uTest(List<Information> genes, List<Information> nonGenes, String flag) {
        List<Double> geneRate = rate(genes, flag);
        List<Double> nonGeneRate = rate(nonGenes, flag);
        double geneMutRateMean = 0.0;
        double nonGeneMutRateMean = 0.0;
        double geneSum = 0.0;
        double nonGeneSum = 0.0;
        double geneVariance = 0.0;
        double nonGeneVariance = 0.0;
        double u = 0.0;

        geneMutRateMean = mean(geneRate);
        nonGeneMutRateMean = mean(nonGeneRate);
        geneSum = (double) genes.size();
        nonGeneSum = (double) nonGenes.size();
        geneVariance = variance(geneRate, geneMutRateMean);
        nonGeneVariance = variance(nonGeneRate, nonGeneMutRateMean);

        u = (geneMutRateMean - nonGeneMutRateMean) / (sqrt((geneVariance / geneSum) + (nonGeneVariance / nonGeneSum)));
        return u;
    }

    /**
     * 由原始数据计算突变率
     *
     * @param information 输入的序列
     * @param flag        需要计算的突变类型 “AG” 表示由 A 突变为 G
     * @return 返回突变率集合
     */
    List<Double> rate(List<Information> information, String flag) {
        int num = 0;
        int length = 0;
        List<Double> rates = new ArrayList<>();
        for (int i = 0; i < information.size(); i++) {
            length = information.get(i).getEndPos() - information.get(i).getStartPos() + 1;
            if ("AT".equals(flag)) {
                num = information.get(i).getATNum();
            } else if ("AG".equals(flag)) {
                num = information.get(i).getAGNum();
            } else if ("AC".equals(flag)) {
                num = information.get(i).getACNum();
            } else if ("TA".equals(flag)) {
                num = information.get(i).getTANum();
            } else if ("TG".equals(flag)) {
                num = information.get(i).getTGNum();
            } else if ("TC".equals(flag)) {
                num = information.get(i).getTCNum();
            } else if ("GA".equals(flag)) {
                num = information.get(i).getGANum();
            } else if ("GT".equals(flag)) {
                num = information.get(i).getGTNum();
            } else if ("GC".equals(flag)) {
                num = information.get(i).getGCNum();
            } else if ("CA".equals(flag)) {
                num = information.get(i).getACNum();
            } else if ("CT".equals(flag)) {
                num = information.get(i).getCTNum();
            } else if ("CG".equals(flag)) {
                num = information.get(i).getCGNum();
            }
            rates.add((double) num / (double) length);
        }
        return rates;
    }

    /**
     * 计算突变率均值
     *
     * @param rates 突变率集合
     * @return 返回均值
     */
    double mean(List<Double> rates) {
        double sum = 0.0;
        for (int i = 0; i < rates.size(); i++) {
            //Double是引用数据类型 此处 if 语句用于防止自动装箱时产生误差
            if (rates.get(i) > 0.0) {
                sum += rates.get(i);
            }
        }
        return sum / rates.size();
    }

    /**
     * 计算方差
     *
     * @param rates 突变率集合
     * @param mean  突变率均值
     * @return 返回方差
     */
    double variance(List<Double> rates, double mean) {
        double sum = 0.0;
        for (int i = 0; i < rates.size(); i++) {
            //Double是引用数据类型 此处 if 语句用于防止自动装箱时产生误差
            if (rates.get(i) > 0.0) {
                sum += pow((rates.get(i) - mean), (double) 2);
            }
        }
        return sum / (rates.size() - 1);
    }
}
