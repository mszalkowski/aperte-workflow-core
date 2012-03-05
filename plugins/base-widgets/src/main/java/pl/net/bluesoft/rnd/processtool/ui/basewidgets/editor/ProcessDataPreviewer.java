package pl.net.bluesoft.rnd.processtool.ui.basewidgets.editor;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import pl.net.bluesoft.rnd.processtool.model.ProcessInstance;
import pl.net.bluesoft.rnd.processtool.model.config.ProcessStateConfiguration;
import pl.net.bluesoft.rnd.processtool.model.config.ProcessStateWidget;
import pl.net.bluesoft.rnd.processtool.ui.basewidgets.ProcessDataBlockWidget;
import pl.net.bluesoft.rnd.processtool.ui.basewidgets.xml.jaxb.WidgetsDefinitionElement;
import pl.net.bluesoft.rnd.util.i18n.I18NSource;

import java.util.*;
import static pl.net.bluesoft.rnd.processtool.ui.basewidgets.editor.EditorHelper.*;

/**
 * @author tlipski@bluesoft.net.pl
 */
public class ProcessDataPreviewer extends ProcessDataBlockWidget {
   
   
//  workaround necessary to access scripting properties
    private Map<String, Property> formProperties;
    private VerticalLayout compositionRoot;
    
    public ProcessDataPreviewer() {
        
        //fake context initialization
        setContext(new ProcessStateConfiguration(),
                new ProcessStateWidget(), getDumbI18nSource(), null, getDumbApplication(),
                new HashSet<String>(Arrays.asList("EDIT")), true);
    }

    private Application getDumbApplication() {
        return new Application() {
            @Override
            public void init() {
                //nothing
            }
        };
    }

    private I18NSource getDumbI18nSource() {
        return new I18NSource() {

            @Override
            public String getMessage(String key) {
                return key;
            }

            @Override
            public String getMessage(String key, String defaultValue) {
                return key;
            }

            @Override
            public Locale getLocale() {
                return Locale.getDefault();
            }

            @Override
            public void setLocale(Locale locale) {
                //nothing
            }

            @Override
            public String getMessage(String key, Object... params) {
                return key;
            }

            @Override
            public String getMessage(String key, String defaultValue, Object... params) {
                return key;
            }
        };
    }

    public Component render(WidgetsDefinitionElement element, Map<String, Property> form) {

        formProperties = form;
        compositionRoot = new VerticalLayout();

        loadData(new ProcessInstance());
        widgetsDefinitionElement = element;
        Component rendered = null;
        try {
            rendered = render();
        } catch (Exception e) {
            String message = e.getMessage();
//            get wrapped exception
            if(e.getCause() != null)
                message = e.getCause().getMessage();
            rendered = new Label(getLocalizedMessage("preview.script.error") + message, Label.CONTENT_XHTML);
        }
        VerticalLayout vl = new VerticalLayout();
        vl.setWidth("100%");
        Button refresh = new Button(getLocalizedMessage("preview.refresh"));
        vl.addComponent(rendered);
        vl.addComponent(refresh);

        refresh.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                compositionRoot.removeAllComponents();
                compositionRoot.addComponent(render(widgetsDefinitionElement, formProperties));
            }
        });
        compositionRoot.addComponent(vl);
        return compositionRoot;
    }

    /**
     * Wrap and rethrow exceptions
     * @param message
     * @param e
     */
    @Override
    protected void handleException(String message, Exception e) {
         throw new RuntimeException(e);
    }

    @Override
    public String getScriptEngineType() {
        return formProperties.get("scriptEngineType") !=null ? (String) formProperties.get("scriptEngineType").getValue() : null;
    }

    @Override
    public String getScriptSourceCode() {
        return formProperties.get("scriptSourceCode") !=null ? (String) formProperties.get("scriptSourceCode").getValue() : null;
    }

    @Override
    public String getScriptUrl() {
        return formProperties.get("scriptUrl") !=null ? (String) formProperties.get("scriptUrl").getValue() : null;
    }
}
