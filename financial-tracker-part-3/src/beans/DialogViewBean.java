package beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DialogFrameworkOptions;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.jsf.primefaces.dialog.IvyDynamicDialog;

@ManagedBean(name = "dialogViewBean")
@ViewScoped
public class DialogViewBean implements Serializable {
  private static final long serialVersionUID = 1L;

  @ManagedProperty(value = "#{dialogDataStore}")
  private DialogDataStore dialogDataStore;

  public void perform(String action, String view, Integer width, Integer height, Boolean modal, Boolean resizable,
      Boolean draggable, Map<String, Object> options, Map<String, List<String>> params, Object data,
      String dataKeyParam) {
    if ("close".equalsIgnoreCase(action)) {
      close(view);
      return;
    }
    open(view, width, height, modal, resizable, draggable, options, params, data, dataKeyParam);
  }

  public void open(String view, Integer width, Integer height, Boolean modal, Boolean resizable,
      Boolean draggable, Map<String, Object> options, Map<String, List<String>> params, Object data,
      String dataKeyParam) {
    if (view == null || view.trim().isEmpty()) {
      Ivy.log().warn("Dialog view is empty, dialog will not open.");
      return;
    }

    String viewName = view.trim();
    boolean absolutePath = viewName.startsWith("/");
    if (absolutePath) {
      if (!viewName.endsWith(".xhtml")) {
        viewName = viewName + ".xhtml";
      }
    } else if (viewName.endsWith(".xhtml")) {
      viewName = viewName.substring(0, viewName.length() - ".xhtml".length());
    }

    Map<String, List<String>> dialogParams = prepareParams(params, data, dataKeyParam);

    boolean hasOptions = width != null || height != null || modal != null || resizable != null || draggable != null
        || (options != null && !options.isEmpty());
    if (!hasOptions && (dialogParams == null || dialogParams.isEmpty())) {
      if (absolutePath) {
        PrimeFaces.current().dialog().openDynamic(viewName);
      } else {
        new IvyDynamicDialog().open(viewName);
      }
      return;
    }

    DialogFrameworkOptions.Builder builder = DialogFrameworkOptions.builder();
    if (width != null) {
      builder.contentWidth(String.valueOf(width));
    }
    if (height != null) {
      builder.contentHeight(String.valueOf(height));
    }
    if (modal != null) {
      builder.modal(modal);
    }
    if (resizable != null) {
      builder.resizable(resizable);
    }
    if (draggable != null) {
      builder.draggable(draggable);
    }
    applyOptions(builder, options);

    DialogFrameworkOptions builtOptions = builder.build();
    if (absolutePath) {
      PrimeFaces.current().dialog().openDynamic(viewName, builtOptions.toMap(), dialogParams);
    } else {
      new IvyDynamicDialog().open(viewName, builtOptions, dialogParams);
    }
  }

  public void close(String view) {
    if (view == null || view.trim().isEmpty()) {
      Ivy.log().warn("Dialog view is empty, dialog will not close.");
      return;
    }
    String viewName = view.trim();
    if (viewName.endsWith(".xhtml")) {
      viewName = viewName.substring(0, viewName.length() - ".xhtml".length());
    }
    new IvyDynamicDialog().close(viewName);
  }

  private static void applyOptions(DialogFrameworkOptions.Builder builder, Map<String, Object> options) {
    if (options == null || options.isEmpty()) {
      return;
    }
    for (Map.Entry<String, Object> entry : options.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (key == null || value == null) {
        continue;
      }
      switch (key) {
      case "width":
        builder.width(String.valueOf(value));
        break;
      case "height":
        builder.height(String.valueOf(value));
        break;
      case "contentWidth":
        builder.contentWidth(String.valueOf(value));
        break;
      case "contentHeight":
        builder.contentHeight(String.valueOf(value));
        break;
      case "modal":
        applyBoolean(builder::modal, value);
        break;
      case "resizable":
        applyBoolean(builder::resizable, value);
        break;
      case "draggable":
        applyBoolean(builder::draggable, value);
        break;
      case "closable":
        applyBoolean(builder::closable, value);
        break;
      case "closeOnEscape":
        applyBoolean(builder::closeOnEscape, value);
        break;
      case "fitViewport":
        applyBoolean(builder::fitViewport, value);
        break;
      case "responsive":
        applyBoolean(builder::responsive, value);
        break;
      case "blockScroll":
        applyBoolean(builder::blockScroll, value);
        break;
      case "styleClass":
        builder.styleClass(String.valueOf(value));
        break;
      default:
        Ivy.log().warn("Unsupported dialog option: " + key);
        break;
      }
    }
  }

  private Map<String, List<String>> prepareParams(Map<String, List<String>> params, Object data,
      String dataKeyParam) {
    Map<String, List<String>> dialogParams = params == null ? new HashMap<>() : new HashMap<>(params);
    if (data != null) {
      if (dialogDataStore == null) {
        Ivy.log().warn("DialogDataStore not available, data will not be passed to dialog.");
      } else {
        String key = dialogDataStore.put(data);
        String paramName = (dataKeyParam == null || dataKeyParam.trim().isEmpty()) ? "dialogDataKey"
            : dataKeyParam.trim();
        dialogParams.put(paramName, Collections.singletonList(key));
      }
    }
    return dialogParams;
  }

  private static void applyBoolean(java.util.function.Consumer<Boolean> setter, Object value) {
    if (value instanceof Boolean) {
      setter.accept((Boolean) value);
    } else {
      setter.accept(Boolean.parseBoolean(String.valueOf(value)));
    }
  }

  public DialogDataStore getDialogDataStore() {
    return dialogDataStore;
  }

  public void setDialogDataStore(DialogDataStore dialogDataStore) {
    this.dialogDataStore = dialogDataStore;
  }
}