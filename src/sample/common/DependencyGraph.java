package sample.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependencyGraph {

    private final Pattern WEIGHTPATTERN = Pattern.compile("Weight=(\\d+)");
    private final Pattern NODENAMEPATTERN = Pattern.compile("\t(\\d+)\t");
    private final Pattern EDGESOURCEPATTERN = Pattern.compile("\t(\\d+) ");
    private final Pattern EDGEDESTPATTERN = Pattern.compile(" (\\d+)\t");

    private Matcher _weightMatcher;
    private Matcher _nodeNameMatcher;
    private Matcher _edgeSourcePatternMatcher;
    private Matcher _edgeDestPatternMatcher;
    private DependencyGraph dg = DependencyGraph.getGraph();

    private static DependencyGraph _dg;
    private List<TaskDependencyNode> _nodes = new ArrayList<TaskDependencyNode>();

    private DependencyGraph(){
    }

    public static DependencyGraph getGraph(){
        if(_dg == null){
            _dg = new DependencyGraph();
        }
        return _dg;
    }

    public List<TaskDependencyNode> getCandidate(){
        return null;
    }

    public boolean addNode(TaskDependencyNode node){
        return(_nodes.add(node));
    }

    public boolean addAllNodes(Collection nodes){
        return(_nodes.addAll(nodes));
    }



    //    Parsing Assumptions:
//    A node occurs in the dot file prior to any of its edges either to or from
//    A edge always contains the '-' character as a part of the arrow symbol
//    The first line is always some file header containing the word "digraph"
    public void parse() {
        try {
            String filePath = "Input/example-input-graphs/Nodes_7_OutTree.dot";
            BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
            String s = br.readLine();
            while (s != null) {
                if (s.toLowerCase().contains("digraph")) {
                }else if(s.contains("-")){
                    _edgeSourcePatternMatcher = EDGESOURCEPATTERN.matcher(s);
                    _edgeSourcePatternMatcher.find();
                    _edgeDestPatternMatcher = EDGEDESTPATTERN.matcher(s);
                    _edgeDestPatternMatcher.find();
                    _weightMatcher = WEIGHTPATTERN.matcher(s);
                    _weightMatcher.find();
                    if(_nodes.contains())
                }else {
                    _nodeNameMatcher = NODENAMEPATTERN.matcher(s);
                    _nodeNameMatcher.find();
                    _weightMatcher = WEIGHTPATTERN.matcher(s);
                    _weightMatcher.find();
                    _nodes.add(new TaskDependencyNode(Integer.valueOf(_weightMatcher.group(1)), _nodeNameMatcher.group(1)));
                }
                s = br.readLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
