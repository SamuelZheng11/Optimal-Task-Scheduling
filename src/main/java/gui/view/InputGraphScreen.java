package gui.view;

import javafx.embed.swing.SwingNode;
import org.graphstream.graph.Graph;
import org.graphstream.ui.layout.HierarchicalLayout;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.layout.Layouts;

import javax.swing.*;


public class InputGraphScreen {

    private SwingNode _swingNode;

    public InputGraphScreen(SwingNode swingNode, Graph graph){
        _swingNode = swingNode;

        graph.addAttribute("ui.stylesheet", "graph { fill-color: grey; }");

        //Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

        Viewer viewer = new Viewer(graph,Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

        HierarchicalLayout layout = new HierarchicalLayout();
        viewer.enableAutoLayout(layout);

        viewer.addDefaultView(false);   // false indicates "no JFrame".
        JPanel view = viewer.getDefaultView();

        _swingNode.setContent(view);

    }

    public void drawInputGraph(){

    }

}
