package com.zidongzh.test;

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
import java.util.List;


import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

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
//        System.out.println(nonGenes);

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

        //将基因与非基因添加到数据库
        for (int i = 0; i < genes.size(); i++) {
            informationMapper.addGeneInfo(genes.get(i));
        }
        for (int i = 0; i < nonGenes.size(); i++) {
            informationMapper.addNonGeneInfo(nonGenes.get(i));
        }
        //漏掉三个

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

        List<Mutation> mutations = mutationMapper.getAll();

        List<Mutation> ATMutations = new ArrayList<>();
        List<Mutation> AGMutations = new ArrayList<>();
        List<Mutation> ACMutations = new ArrayList<>();
        List<Mutation> TAMutations = new ArrayList<>();
        List<Mutation> TGMutations = new ArrayList<>();
        List<Mutation> TCMutations = new ArrayList<>();
        List<Mutation> GAMutations = new ArrayList<>();
        List<Mutation> GTMutations = new ArrayList<>();
        List<Mutation> GCMutations = new ArrayList<>();
        List<Mutation> CAMutations = new ArrayList<>();
        List<Mutation> CTMutations = new ArrayList<>();
        List<Mutation> CGMutations = new ArrayList<>();

//        mutations.size()
        for (int i = 0; i < mutations.size(); i++) {
            if (mutations.get(i).getType().startsWith("AT")) {
                ATMutations.add(mutations.get(i));
            } else if (mutations.get(i).getType().startsWith("AG")) {
                AGMutations.add(mutations.get(i));
            } else if (mutations.get(i).getType().startsWith("AC")) {
                ACMutations.add(mutations.get(i));
            } else if (mutations.get(i).getType().startsWith("TA")) {
                TAMutations.add(mutations.get(i));
            } else if (mutations.get(i).getType().startsWith("TG")) {
                TGMutations.add(mutations.get(i));
            } else if (mutations.get(i).getType().startsWith("TC")) {
                TCMutations.add(mutations.get(i));
            } else if (mutations.get(i).getType().startsWith("GA")) {
                GAMutations.add(mutations.get(i));
            } else if (mutations.get(i).getType().startsWith("GT")) {
                GTMutations.add(mutations.get(i));
            } else if (mutations.get(i).getType().startsWith("GC")) {
                GCMutations.add(mutations.get(i));
            } else if (mutations.get(i).getType().startsWith("CA")) {
                CAMutations.add(mutations.get(i));
            } else if (mutations.get(i).getType().startsWith("CT")) {
                CTMutations.add(mutations.get(i));
            } else if (mutations.get(i).getType().startsWith("CG")) {
                CGMutations.add(mutations.get(i));
            }
        }
        System.out.println("all add up is " + (ATMutations.size() + AGMutations.size() + ACMutations.size() + TAMutations.size() + TGMutations.size() + TCMutations.size() + GAMutations.size() +
                GTMutations.size() + GCMutations.size() + CAMutations.size() + CTMutations.size() + CGMutations.size()));
        System.out.println("mutation size is " + mutations.size());

//        System.out.println(ATMutations);
//        System.out.println("-----------------");
//        System.out.println(AGMutations);
//        System.out.println("-----------------");
//        System.out.println(ACMutations);
//        System.out.println("-----------------");
//        System.out.println(TAMutations);
//        System.out.println("-----------------");
//        System.out.println(TGMutations);
//        System.out.println("-----------------");
//        System.out.println(TCMutations);
//        System.out.println("-----------------");
//        System.out.println(CAMutations);
//        System.out.println("-----------------");
//        System.out.println(CTMutations);
//        System.out.println("-----------------");
//        System.out.println(CGMutations);
//        System.out.println("-----------------");
//        System.out.println(GAMutations);
//        System.out.println("-----------------");
//        System.out.println(GTMutations);
//        System.out.println("-----------------");
//        System.out.println(GCMutations);


    }


    void classify(Information gene, Mutation mutation) {
        if (mutation.getChromosomePos() >= gene.getStartPos() &&
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
     * @param genes       基因信息
     * @param chromosomes 染色体信息
     * @return 非基因区间
     */
    List<Information> getNonGenes(List<Information> genes,
                                  List<Information> chromosomes) {
        int pos = 0;

        List<Information> nonGenes = new ArrayList<>();
        Information nonGeneHead = new Information(chromosomes.get(0).getChromosomeId(), "nonGene", 1, genes.get(0).getStartPos() - 1);
        nonGenes.add(nonGeneHead);
        for (int i = 0; i < chromosomes.size(); i++) {
            for (int j = 0; j < genes.size(); j++) {
                if (
                        genes.get(j).getChromosomeId().equals(chromosomes.get(i).getChromosomeId()) &&
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

}
