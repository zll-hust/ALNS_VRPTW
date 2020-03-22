package zll.vrptw.alns.repair;

import zll.vrptw.algrithm.MyALNSSolution;
import zll.vrptw.alns.operation.IALNSOperation;

public interface IALNSRepair extends IALNSOperation {

    MyALNSSolution repair(MyALNSSolution from);
}
