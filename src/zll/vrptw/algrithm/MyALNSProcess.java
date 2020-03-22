package zll.vrptw.algrithm;

import java.io.IOException;
import java.util.Random;

import zll.vrptw.alns.config.ControlParameter;
import zll.vrptw.alns.config.IALNSConfig;
import zll.vrptw.alns.destroy.IALNSDestroy;
import zll.vrptw.alns.destroy.RandomDestroy;
import zll.vrptw.alns.destroy.ShawDestroy;
import zll.vrptw.alns.destroy.WorstCostDestroy;
import zll.vrptw.alns.repair.GreedyRepair;
import zll.vrptw.alns.repair.IALNSRepair;
import zll.vrptw.alns.repair.RandomRepair;
import zll.vrptw.alns.repair.RegretRepair;
import zll.vrptw.instance.Instance;

public class MyALNSProcess {
	// 可视化
    //private final ALNSObserver o = new ALNSObserver();
    // 可视化，针对alns进程
    //private final ALNSProcessVisualizationManager apvm = new ALNSProcessVisualizationManager();
    // 参数
    private final IALNSConfig config;
    private final IALNSDestroy[] destroy_ops = new IALNSDestroy[]{
            //new ProximityZoneDestroy(),
            //new ZoneDestroy(),
            //new NodesCountDestroy(false),
            //new SubrouteDestroy(),
            new ShawDestroy(),
            new RandomDestroy(),
            new WorstCostDestroy()
    };
    private final IALNSRepair[] repair_ops = new IALNSRepair[]{
            new RegretRepair(),
            new GreedyRepair(),
            new RandomRepair()
    };

    private final double T_end_t = 0.01;
    // 全局满意解
    private MyALNSSolution s_g = null;
    // 局部满意解
    private MyALNSSolution s_c = null;
    private boolean cpng = false;
    private int i = 0;
    // time
    private double T;
    private double T_s;
    // time start
    private long t_start;
    // time end
    private double T_end;
    

    public MyALNSProcess(Solution s_, Instance instance, IALNSConfig c, ControlParameter cp) throws InterruptedException {
        
    	// 生成png
    	cpng = cp.isSolutionImages();
    	
        config = c;
        s_g = new MyALNSSolution(s_, instance);
        s_c = new MyALNSSolution(s_g);
        
        // 初始化alns参数
        initStrategies();
        
        // 可视化
        if (cp.isSolutionsLinechart()) {
            //o.add(new SolutionsLinechart(this));
        }
        if (cp.isOperationsLinechart()) {
            //o.add(new OperationsLinechart(this));
        }
    }

    public Solution improveSolution() throws Exception {
        //o.onThreadStart(this);
    	
        T_s = -(config.getDelta() / Math.log(config.getBig_omega())) * s_c.cost.total;
        T = T_s;
        T_end = T_end_t * T_s;
        
        // 计时开始
        t_start = System.currentTimeMillis();
        //o.onStartConfigurationObtained(this);
        
        while (true) {
        	
        	// sc局部最优解，从局部最优中生成新解
            MyALNSSolution s_c_new = new MyALNSSolution(s_c);
            int q = getQ(s_c_new);
            
            // 轮盘赌找出最优destroy、repair方法
            IALNSDestroy destroyOperator = getALNSDestroyOperator();
            IALNSRepair repairOperator = getALNSRepairOperator();
            //o.onDestroyRepairOperationsObtained(this, destroyOperator, repairOperator, s_c_new, q);

            // destroy solution
            MyALNSSolution s_destroy = destroyOperator.destroy(s_c_new, q);
            //o.onSolutionDestroy(this, s_destroy);

            // repair solution，重组后新解st
            MyALNSSolution s_t = repairOperator.repair(s_destroy);
            //o.onSolutionRepaired(this, s_t);

            System.out.println("迭代次数 ：" +  i + "当前解 ：" + Math.round(s_t.cost.total * 100) / 100.0);
            
            // 更新局部满意解
            if (s_t.cost.total < s_c.cost.total) {
                s_c = s_t;
                // 更新全局满意解，sg全局满意解
                if (s_t.cost.total < s_g.cost.total) {
                    handleNewGlobalMinimum(destroyOperator, repairOperator, s_t);
                } else {
                	// 更新局部满意解
                    handleNewLocalMinimum(destroyOperator, repairOperator);
                }
            } else {
            	// 概率接受较差解
                handleWorseSolution(destroyOperator, repairOperator, s_t);
            }
            //o.onAcceptancePhaseFinsihed(this, s_t);
            
            if (i % config.getTau() == 0 && i > 0) {
                segmentFinsihed();
                //o.onSegmentFinsihed(this, s_t);
            }
            
            T = config.getC() * T;
            //o.onIterationFinished(this, s_t);
            i++;
            
            if (i > config.getOmega() && s_g.feasible()) break;
            if (i > config.getOmega() * 1.5 ) break;
        }
        
        Solution solution = s_g.toSolution();
        
        // 输出程序耗时s
        double s = Math.round((System.currentTimeMillis() - t_start) * 1000) / 1000000.;
        System.out.println("\nALNS progress cost " + s + "s.");
        
        // 输出算子使用情况
        for (IALNSDestroy destroy : destroy_ops){
        	System.out.println(destroy.getClass().getName() + 
        			" 被使用 " + destroy.getDraws() + "次.");
        }
        
        for (IALNSRepair repair : repair_ops){
        	System.out.println(repair.getClass().getName() + 
        			" 被使用 " + repair.getDraws() + "次.");
        }
        solution.testTime = s;
        return solution;
    }

    private void handleWorseSolution(IALNSDestroy destroyOperator, IALNSRepair repairOperator, MyALNSSolution s_t) {
        //概率接受较差解
    	double p_accept = calculateProbabilityToAcceptTempSolutionAsNewCurrent(s_t);
        if (Math.random() < p_accept) {
            s_c = s_t;
        }
        destroyOperator.addToPi(config.getSigma_3());
        repairOperator.addToPi(config.getSigma_3());
    }

    private void handleNewLocalMinimum(IALNSDestroy destroyOperator, IALNSRepair repairOperator) {
        destroyOperator.addToPi(config.getSigma_2());
        repairOperator.addToPi(config.getSigma_2());
    }

    private void handleNewGlobalMinimum(IALNSDestroy destroyOperator, IALNSRepair repairOperator, MyALNSSolution s_t) throws IOException {
        //System.out.println(String.format("[%d]: Found new global minimum: %.2f, Required Vehicles: %d, I_uns: %d", i, s_t.getCostFitness(), s_t.activeVehicles(), s_g.getUnscheduledJobs().size()));
        if (this.cpng) {
            //TODO OutputUtil.createPNG(s_t, i);
        }
        //接受全局较优
        s_g = s_t;
        destroyOperator.addToPi(config.getSigma_1());
        repairOperator.addToPi(config.getSigma_1());
    }

    private double calculateProbabilityToAcceptTempSolutionAsNewCurrent(MyALNSSolution s_t) {
        return Math.exp (-(s_t.cost.total - s_c.cost.total) / T);
    }

    private int getQ(MyALNSSolution s_c2) {
        int q_l = Math.min((int) Math.ceil(0.05 * s_c2.instance.getCustomerNr()), 10);
        int q_u = Math.min((int) Math.ceil(0.20 * s_c2.instance.getCustomerNr()), 30);

        Random r = new Random();
        return r.nextInt(q_u - q_l + 1) + q_l;
    }


    private void segmentFinsihed() {
        double w_sum = 0;
        // Update neue Gewichtung der Destroy Operatoren
        for (IALNSDestroy dstr : destroy_ops) {
            double w_old1 = dstr.getW() * (1 - config.getR_p());
            double recentFactor = dstr.getDraws() < 1 ? 0 : (double) dstr.getPi() / (double) dstr.getDraws();
            double w_old2 = config.getR_p() * recentFactor;
            double w_new = w_old1 + w_old2;
            w_sum += w_new;
            dstr.setW(w_new);
        }
        // Update neue Wahrs. der Destroy Operatoren
        for (IALNSDestroy dstr : destroy_ops) {
            dstr.setP(dstr.getW() / w_sum);
            //dstr.setDraws(0);
            //dstr.setPi(0);
        }
        w_sum = 0;
        // Update neue Gewichtung der Repair Operatoren
        for (IALNSRepair rpr : repair_ops) {
            double recentFactor = rpr.getDraws() < 1 ? 0 : (double) rpr.getPi() / (double) rpr.getDraws();
            double w_new = (rpr.getW() * (1 - config.getR_p())) + config.getR_p() * recentFactor;
            w_sum += w_new;
            rpr.setW(w_new);
        }
        // Update neue Wahrs. der Repair Operatoren
        for (IALNSRepair rpr : repair_ops) {
            rpr.setP(rpr.getW() / w_sum);
            //rpr.setDraws(0);
            //rpr.setPi(0);
        }
    }


    private IALNSRepair getALNSRepairOperator() {
        double random = Math.random();
        double threshold = 0.;
        for (IALNSRepair rpr : repair_ops) {
            threshold += rpr.getP();
            if (random <= threshold) {
                rpr.drawn();
                return rpr;
            }
        }
        repair_ops[repair_ops.length - 1].drawn();
        return repair_ops[repair_ops.length - 1];
    }


    private IALNSDestroy getALNSDestroyOperator() {
        double random = Math.random();
        double threshold = 0.;
        for (IALNSDestroy dstr : destroy_ops) {
            threshold += dstr.getP();
            if (random <= threshold) {
                dstr.drawn();
                return dstr;
            }
        }
        
        destroy_ops[destroy_ops.length - 1].drawn();
        return destroy_ops[destroy_ops.length - 1];
    }


    private void initStrategies() {
        for (IALNSDestroy dstr : destroy_ops) {
        	dstr.setDraws(0);
            dstr.setPi(0);
            dstr.setW(1.);
            dstr.setP(1 / (double) destroy_ops.length);
        }
        for (IALNSRepair rpr : repair_ops) {
            rpr.setDraws(0);
        	rpr.setPi(0);
            rpr.setW(1.);
            rpr.setP(1 / (double) repair_ops.length);
        }
    }
    /*
    public ALNSObserver getO() {
        return this.o;
    }

    public ALNSProcessVisualizationManager getApvm() {
        return this.apvm;
    }
	*/
    public IALNSConfig getConfig() {
        return this.config;
    }

    public IALNSDestroy[] getDestroy_ops() {
        return this.destroy_ops;
    }

    public IALNSRepair[] getRepair_ops() {
        return this.repair_ops;
    }

    public MyALNSSolution getS_g() {
        return this.s_g;
    }

    public MyALNSSolution getS_c() {
        return this.s_c;
    }

    public boolean isCpng() {
        return this.cpng;
    }

    public int getI() {
        return this.i;
    }

    public double getT() {
        return this.T;
    }

    public double getT_s() {
        return this.T_s;
    }

    public long getT_start() {
        return this.t_start;
    }

    public double getT_end_t() {
        return this.T_end_t;
    }

    public double getT_end() {
        return this.T_end;
    }
}
