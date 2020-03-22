package zll.vrptw.alns.destroy;

import java.util.ArrayList;
import java.util.Collections;

import zll.vrptw.algrithm.MyALNSSolution;
import zll.vrptw.alns.operation.ALNSAbstractOperation;
import zll.vrptw.instance.Node;
import zll.vrptw.instance.Route;
import zll.vrptw.instance.Instance;

/**  
* <p>Title: WorstCostDestroy</p>  
* <p>Description: </p>  
* @author zll_hust  
* @date 2020年3月19日  
*/
public class WorstCostDestroy extends ALNSAbstractOperation implements IALNSDestroy {
    /*
	@Override
	public ALNSStrategieVisualizationManager getVisualizationManager() {
		// TODO Auto-generated method stub
		return null;
	}
	*/
	@Override
	public MyALNSSolution destroy(MyALNSSolution s, int removeNr) throws Exception {
		
		if(s.removalCustomers.size() != 0) {
			System.err.println("removalCustomers is not empty.");
			return s;
		}
        
		// 计算fitness值，对客户进行评估。
		ArrayList<Fitness> customerFitness = new  ArrayList<Fitness>();
        for(Route route : s.routes) {
        	for (Node customer : route.getRoute()) {
        		double fitness = Fitness.calculateFitness(s.instance, customer, route);
        		customerFitness.add(new Fitness(customer.getId(), fitness));
        	}
    	}
        Collections.sort(customerFitness);

        ArrayList<Integer> removal = new ArrayList<Integer>();
        for(int i = 0; i < removeNr; ++i) removal.add(customerFitness.get(i).customerNo);
        
        for(int j = 0; j < s.routes.size(); j++) {
        	for (int i = 0; i < s.routes.get(j).getRoute().size();++i) {
        		Node customer = s.routes.get(j).getRoute().get(i);
        		if(removal.contains(customer.getId())) {
        			s.removeCustomer(j, i);
        		}	
        	} 
    	}
    	
        return s;
    }
}

class Fitness implements Comparable<Fitness>{
	public int customerNo;
	public double fitness;
	
	public Fitness() {}
	
	public Fitness(int cNo, double f) {
		customerNo = cNo;
		fitness = f;
	}
	
	public static double calculateFitness(Instance instance, Node customer, Route route) {
		double[][] distance = instance.getDistanceMatrix();
		
		double fitness = 
				(route.getCost().getTimeViolation() + route.getCost().getLoadViolation() + customer.getDemand()) * 
				( distance[customer.getId()][route.getRoute().get(0).getId()] +
				distance[route.getRoute().get(0).getId()][customer.getId()] );
	
		return fitness;
	}
	
	@Override
	public int compareTo(Fitness o) {
		Fitness s = (Fitness) o;
		if (s.fitness > this.fitness  ) {
			return 1;
		} else if (this.fitness == s.fitness) {
			return 0;
		} else {
			return -1;
		}
	}

}
