import com.vaadin.Application;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class TestListeners extends Application implements LayoutClickListener,
        ClickListener, FocusListener {

    @Override
    public void init() {
        Window w = new Window("main window");
        setMainWindow(w);
        HorizontalLayout hl = new HorizontalLayout();
        w.setContent(hl);
        // Panel p = new Panel("My panel");
        // p.addListener(new ClickListener() {
        //
        // public void click(ClickEvent event) {
        // getMainWindow().showNotification("Clicked!");
        //
        // }
        // });
        // w.addComponent(p);
        // if (true) {
        // return;
        // }
        // VerticalLayout vl = new VerticalLayout();

        final AbsoluteLayout al = new AbsoluteLayout();
        al.setWidth("200px");
        al.setHeight("200px");
        al.addComponent(new TextField("This is its caption",
                "This is a textfield"), "top: 20px; left: 0px; width: 100px;");
        al.addComponent(new TextField("Antoerh caption",
                "This is another textfield"),
                "top: 120px; left: 0px; width: 100px;");

        final LayoutClickListener lcl = new LayoutClickListener() {

            public void layoutClick(LayoutClickEvent event) {
                System.out.println("AL: Click on " + event.getChildComponent());
                // al.removeListener(this);

            }
        };
        al.addListener(lcl);

        final GridLayout vl = new GridLayout();
        vl.addComponent(al);
        vl.setSpacing(true);
        for (int i = 0; i < 10; i++) {
            vl.addComponent(new Label("Component " + i));
            ExternalResource res = new ExternalResource(
                    "http://vaadin.com/image/image_gallery?uuid=07c1f6d5-2e94-4f4d-a707-b548bf22279d&groupId=10919&t=1241012632062");
            Embedded e = new Embedded("an image", res);
            e.setType(Embedded.TYPE_IMAGE);
            e.addListener(new ClickListener() {

                public void click(ClickEvent event) {
                    TestListeners.this.click(event);

                }
            });
            // e.addListener(this);
            vl.addComponent(e);
            TextField tf = new TextField("tf");
            tf.setInputPrompt("Please enter a value");

            // tf.addListener(this);
            tf.addListener(new BlurListener() {

                public void blur(BlurEvent event) {
                    getMainWindow().showNotification(
                            "Blurred " + event.getComponent());

                }
            });
            tf.addListener(new FocusListener() {

                public void focus(FocusEvent event) {
                    getMainWindow().showNotification(
                            "Focused " + event.getComponent());

                }
            });
            vl.addComponent(tf);
        }

        // vl.addListener(this);
        vl.addListener(new LayoutClickListener() {

            public void layoutClick(LayoutClickEvent event) {
                TestListeners.this.layoutClick(event);
                vl.removeListener(this);
            }
        });
        vl.setMargin(true);

        hl.addComponent(vl);
        hl.addComponent(createClickableGridLayout());
        hl.addComponent(createClickableVerticalLayout());
    }

    private Layout createClickableGridLayout() {

        GridLayout gl = new GridLayout(3, 3);
        addContent(gl, 5);

        gl.addListener(new LayoutClickListener() {

            public void layoutClick(LayoutClickEvent event) {
                getMainWindow().showNotification(
                        "GL-click on " + event.getChildComponent());

            }
        });

        return wrap(gl, "Clickable GridLayout");
    }

    private Layout createClickableVerticalLayout() {

        VerticalLayout gl = new VerticalLayout();
        addContent(gl, 5);

        gl.addListener(new LayoutClickListener() {

            public void layoutClick(LayoutClickEvent event) {
                getMainWindow().showNotification(
                        "VL-click on " + event.getChildComponent());

            }
        });

        return wrap(gl, "Clickable VerticalLayout");
    }

    private void addContent(Layout gl, int nr) {
        for (int i = 1; i < nr; i++) {
            Label l = new Label("This is label " + i);
            l.setWidth(null);
            gl.addComponent(l);
        }
        for (int i = nr; i < nr * 2; i++) {
            gl.addComponent(new TextField("This is tf" + i, "this is tf " + i));
        }
    }

    private Layout wrap(Component c, String caption) {
        VerticalLayout vl = new VerticalLayout();
        Label l = new Label(caption);
        l.setWidth(null);
        vl.addComponent(l);
        vl.addComponent(c);

        return vl;
    }

    public void layoutClick(LayoutClickEvent event) {
        if (event.getChildComponent() == null) {
            getMainWindow().showNotification("You missed!");
        } else {
            getMainWindow().showNotification(
                    "Clicked on " + event.getChildComponent() + "!");
            // getMainWindow().removeComponent(event.getChildComponent());
        }

    }

    public void click(ClickEvent event) {
        getMainWindow().showNotification(
                "Clicked on " + event.getComponent() + " using "
                        + event.getButton());
    }

    public void focus(FocusEvent event) {
        TextField tf = (TextField) event.getComponent();
        // tf.addStyleName("a");
        // tf.setValue("");
        getMainWindow().requestRepaintAll();

    }

}
