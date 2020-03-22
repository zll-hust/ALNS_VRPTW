package zll.vrptw.alns.operation;

public interface IALNSOperation {

    int getPi();

    void setPi(int pi);

    void addToPi(int pi);

    double getP();

    void setP(double p);

    double getW();

    void setW(double p);

    void drawn();

    int getDraws();

    void setDraws(int d);

    //ALNSStrategieVisualizationManager getVisualizationManager();

}
