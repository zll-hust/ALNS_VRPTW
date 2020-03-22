package zll.vrptw.algrithm;

import java.util.ArrayList;
import java.util.List;

import zll.vrptw.instance.Route;

/**
 * @author zll_hust
 *
 * An instance of this class repserents a solution to the VRP problem.
 */
public class Solution {
	public double testTime;

    /**
     * All the routes of the current solution.
     */
    private List<Route> routes;

    /**
     * The total cost of the solution. It is calculated as the sum of the costs of all routes.
     */
    private double totalCost;
    
    /**
     * The number of the vehicles.
     */
    private int vehicleNr;

    /**
     * Default constructor
     */
    public Solution() {
        this.routes = new ArrayList<>();
        this.totalCost = 0;
        this.vehicleNr = 0;
    }

    public List<Route> getRoutes() {
        return routes;
    }
    
    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public void addRoute(Route route) {
        this.routes.add(route);
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    
    
    public int getVehicleNr() {
        return vehicleNr;
    }
    
    public void setVehicleNr(int vehicleNr) {
        this.vehicleNr = vehicleNr;
    }
      

    /**
     * This function creates and returns an exact copy of the current solution
     *
     * @return Solution, a copy of this solution.
     */
    public Solution clone() {
        Solution clone = new Solution();

        clone.totalCost = this.totalCost;
        clone.vehicleNr = this.vehicleNr;

        for (Route route: this.routes) {
            clone.routes.add(route.cloneRoute());
        }

        return clone;
    }
    
    @Override
    public String toString() {
        String result = "Solution{" +
                "totalCost=" +  Math.round(totalCost * 100) / 100.0 +
                ", routes=[";

        for (Route vehicle: this.routes) {
        	if (vehicle.getRoute().size() > 2)
        		result += "\n\t" + vehicle;
        }

        return result + "]}";
    }
}