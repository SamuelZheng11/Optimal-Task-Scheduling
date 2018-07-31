package sample.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependencyGraph {

    private static DependencyGraph _dg;
    private Map<String,TaskDependencyNode> _nodes = new HashMap<String,TaskDependencyNode>();

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

    public void addNode(TaskDependencyNode node){
        _nodes.put(node._name, node);
    }



    //    Parsing Assumptions
    //    A node occurs in the dot file prior to any of its edges either to or from
    //    A edge always contains the '-' character as a part of the arrow symbol
    //    The first line is always some file header containing the word "{"
    //    The last line prior to null contains "}"
    public void parse() {
        Pattern WEIGHTPATTERN = Pattern.compile("Weight=(\\d+)");
        Pattern NODENAMEPATTERN = Pattern.compile("\t(\\d+)\t");
        Pattern EDGESOURCEPATTERN = Pattern.compile("\t(\\d+) ");
        Pattern EDGEDESTPATTERN = Pattern.compile(" (\\d+)\t");

        Matcher _weightMatcher;
        Matcher _nodeNameMatcher;
        Matcher _edgeSourcePatternMatcher;
        Matcher _edgeDestPatternMatcher;

        TaskDependencyNode previous;
        try {
            String filePath = "Input/example-input-graphs/Nodes_9_SeriesParallel.dot";
            BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
            String s = br.readLine();
            while (s != null) {
                if (s.contains("{") || s.contains("}") ) {
                }else if(s.contains("-")){
                    _edgeSourcePatternMatcher = EDGESOURCEPATTERN.matcher(s);
                    _edgeSourcePatternMatcher.find();
                    _edgeDestPatternMatcher = EDGEDESTPATTERN.matcher(s);
                    _edgeDestPatternMatcher.find();
                    _weightMatcher = WEIGHTPATTERN.matcher(s);
                    _weightMatcher.find();
                    _nodes.get(_edgeDestPatternMatcher.group(1))._parents.add(new TaskDependencyEdge(_nodes.get(_edgeSourcePatternMatcher.group(1)),_nodes.get(_edgeDestPatternMatcher.group(1)),Integer.valueOf(_weightMatcher.group(1))));
                    _nodes.get(_edgeSourcePatternMatcher.group(1))._children.add(new TaskDependencyEdge(_nodes.get(_edgeSourcePatternMatcher.group(1)),_nodes.get(_edgeDestPatternMatcher.group(1)),Integer.valueOf(_weightMatcher.group(1))));
                }else {
                    _nodeNameMatcher = NODENAMEPATTERN.matcher(s);
                    _nodeNameMatcher.find();
                    _weightMatcher = WEIGHTPATTERN.matcher(s);
                    _weightMatcher.find();
                    previous = new TaskDependencyNode(Integer.valueOf(_weightMatcher.group(1)), _nodeNameMatcher.group(1));
                    _nodes.put(previous._name,previous);
                }
                s = br.readLine();
            }
            for (Map.Entry<String, TaskDependencyNode> entry : _nodes.entrySet())
            {
                System.out.println(entry.getKey() + "/" + entry.getValue());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
