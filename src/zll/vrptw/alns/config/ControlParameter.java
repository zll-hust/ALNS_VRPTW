package zll.vrptw.alns.config;

public class ControlParameter {

    private final boolean solutionsLinechart;
    private final boolean operationsLinechart;
    private final boolean solutionImages;

    public ControlParameter(boolean showSolutionsLinechart, boolean showOperationsLinechart, boolean createSolutionImages) {
        solutionsLinechart = showSolutionsLinechart;
        operationsLinechart = showOperationsLinechart;
        solutionImages = createSolutionImages;
    }

    public boolean isSolutionsLinechart() {
        return this.solutionsLinechart;
    }

    public boolean isOperationsLinechart() {
        return this.operationsLinechart;
    }

    public boolean isSolutionImages() {
        return this.solutionImages;
    }
}
