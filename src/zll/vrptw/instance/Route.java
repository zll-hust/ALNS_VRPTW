package zll.vrptw.instance;

import java.util.ArrayList;
import java.util.List;

import zll.vrptw.algrithm.Cost;


/**
 * @author zll_hust
 *
 * Every instance of this class represents a Route (Vehicle) that will be used in order to serve a set of customers
 */
public class Route {
	
	private int id;
    
    /**
     * A sequence of Customers, that will be served from the current Vehicle.
     */
    private List<Node> route;

    /**
     * The cost of the current Route. It is calculated as the sum of the distances of every next node from the previous one.
     */   
	private Cost cost; 

    /**
     * Constructor
     *
     * @param capacity The capacity for this Vehicle
     */
    public Route(int id) {
        this.route = new ArrayList<>();
        this.id = id;
        this.cost = new Cost();
    }
    
    public Route cloneRoute() {
        Route clone = new Route(this.id);
        clone.cost = new Cost(this.cost);
        clone.route = new ArrayList<>(this.route);

        return clone;
    }
    
    public int getId() {
        return this.id;
    }

    public List<Node> getRoute() {
        return route;
    }

    /**
     * Returns the last node in the route
     */
    public Node getLastNodeOfTheRoute() {
        return this.route.get(this.route.size() - 1);
    }

    /**
     * Adds a customer in the end of the route.
     *
     * @param node The new customer to be inserted.
     */
    public void addNodeToRoute(Node node) {
        this.route.add(node);
    }

    /**
     * Adds a customer in the route in a specific position in the sequence.
     *
     * @param node The new customer to be inserted
     * @param index The position in which the customer will be inserted.
     */
    public void addNodeToRouteWithIndex(Node node, int index) {
        this.route.add(index, node);
    }

    /**
     * Removes a customer from a specific position in the route.
     *
     * @param index The index from which the customer will be removed
     * @return The removed customer.
     */
    public Node removeNode(int index) {
        return this.route.remove(index);
    }

    @Override
    public String toString() {
        String result =  "Route{" +
                "cost = " + this.cost +
                ", route = [";

        for (Node customer: this.route) {
            result += "\n\t\t" + customer;
        }

        return result + "]}";
    }
    
	/**
	 * @return the cost
	 */
	public Cost getCost() {
		return this.cost;
	}
	
	/**
	 * @param cost the cost to set
	 */
	public void setCost(Cost cost) {
		this.cost = cost;
	}

}
