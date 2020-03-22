package zll.vrptw.alns;


import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import zll.vrptw.algrithm.CheckSolution;
import zll.vrptw.algrithm.Solution;
import zll.vrptw.algrithm.Solver;
import zll.vrptw.alns.config.ALNSConfiguration;
import zll.vrptw.alns.config.ControlParameter;
import zll.vrptw.alns.config.IALNSConfig;
import zll.vrptw.instance.Instance;


/**  
* <p>Title: Main</p>  
* <p>Description: </p>  
* @author zll_hust  
* @date 2020年3月14日  
*/
public class Main {

    private static final String[] SOLOMON_ALL = new String[]{
            "C101", "C102", "C103", "C104", "C105", "C106", "C107", "C108", "C109", "C201", "C202", "C203", "C204", "C205", "C206", "C207", "C208",
            "R101", "R102", "R103", "R104", "R105", "R106", "R107", "R108", "R109", "R110", "R111", "R112", "R201", "R202", "R203", "R204", "R205", "R206", "R207", "R208", "R209", "R210", "R211",
            "RC101", "RC102", "RC103", "RC104", "RC105", "RC106", "RC107", "RC108", "RC201", "RC202", "RC203", "RC204", "RC205", "RC206", "RC207", "RC208"
    };
    static String[] SOLOMON_CLUSTERED = new String[]{"C101", "C102", "C103", "C104", "C105", "C106", "C107", "C108", "C109", "C201", "C202", "C203", "C204", "C205", "C206", "C207", "C208"};
    static String[] SOLOMON_RANDOM = new String[]{"R101", "R102", "R103", "R104", "R105", "R106", "R107", "R108", "R109", "R110", "R111", "R112", "R201", "R202", "R203", "R204", "R205", "R206", "R207", "R208", "R209", "R210", "R211",};
    static String[] SOLOMON_CLUSTERRANDOM = new String[]{"RC101", "RC102", "RC103", "RC104", "RC105", "RC106", "RC107", "RC108", "RC201", "RC202", "RC203", "RC204", "RC205", "RC206", "RC207", "RC208"};
    static String[] VRPFD_INSTANCES = new String[]{"C108", "C206", "C203", "R202", "R207", "R104", "RC202", "RC205", "RC208"};
    static String[] Homberger_200 = new String[] {"C1_2_1", "C1_2_2", "C1_2_3", "C1_2_4"};
    static String[] Homberger_400 = new String[] {"C1_4_1", "C1_4_2", "C1_4_3", "C1_4_4"};
    
    public static void main(String args[]) {
    	
        String[] instances = { "C101" };
        String[][] result = new String[instances.length][];
        
        for (int j = 0; j < instances.length; j = j + 1) {
            try {
                result[j] = solve(
                        instances[j],                    //需要测试的算例
                        "Solomon",                      // 算例类型,输入Homberger或Solomon，注意大写
                        25,                        //客户点数量，Solomon可选择 25,50,100，Homberger可选择200，400
                        ALNSConfiguration.DEFAULT,    //ALNS相关参数
                        new ControlParameter(
                                false,                //历史满意解、当前满意解、新解的时序图，收敛效果展示
                                false,                //ALNS算子时序图
                                false                //生成解对应效果图（针对每次迭代的历史满意解）
                        ));
                //printToCSV("ALNS TEST", result, instances.length);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    // solve函数，输出解 输入变量：算例名，客户数，
    private static String[] solve(String name, String instanceType, int size, IALNSConfig c, ControlParameter cp) throws Exception {

        // 输入Solomon算例
        Instance instance = new Instance(size, name, instanceType);
        // 检查结果
        CheckSolution checkSolution = new CheckSolution(instance);
        // 解决策略
        Solver solver = new Solver();
        // 初始解
        Solution is = solver.getInitialSolution(instance);
        //System.out.println(is);
        //System.out.println(checkSolution.Check(is));
        // 满意解
        Solution ims = solver.improveSolution(is, c, cp, instance);
        System.out.println(ims);
        System.out.println(checkSolution.Check(ims));
        
        String[] result = {String.valueOf(ims.getTotalCost()), String.valueOf(ims.testTime)};
        return result;
    }
    
	public static void printToCSV(String FILE_NAME, String[][] result, int size) {
		final String[] FILE_HEADER={"InstanceName", "BestCost", "TimeCost"};
		
		FileWriter fileWriter=null;
		CSVPrinter csvPrinter=null;
		CSVFormat csvFormat=CSVFormat.DEFAULT.withHeader(FILE_HEADER);
		
		try {
			fileWriter=new FileWriter(FILE_NAME + ".csv");
			csvPrinter=new CSVPrinter(fileWriter, csvFormat);
			
			for(int i = 0; i < size; i++){
				List<String> record=new ArrayList<>();
				record.add(Homberger_400[i]);
				record.add(result[i][0]);
				record.add(result[i][1]);
				csvPrinter.printRecord(record);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try{
				fileWriter.flush();
				fileWriter.close();
				csvPrinter.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
