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
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

        graph.addAttribute("ui.stylesheet", "url('graphStyle.css')");

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
