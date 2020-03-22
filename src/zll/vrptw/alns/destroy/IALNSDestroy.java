package zll.vrptw.alns.destroy;

import zll.vrptw.algrithm.MyALNSSolution;
import zll.vrptw.alns.operation.IALNSOperation;

public interface IALNSDestroy extends IALNSOperation {

    MyALNSSolution destroy(MyALNSSolution s, int nodes) throws Exception;

}
