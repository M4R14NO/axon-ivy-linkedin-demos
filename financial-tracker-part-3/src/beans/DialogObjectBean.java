package beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "dialogObjectBean")
@ViewScoped
public class DialogObjectBean implements Serializable {
  private static final long serialVersionUID = 1L;

  @ManagedProperty(value = "#{dialogDataStore}")
  private DialogDataStore dialogDataStore;

  @ManagedProperty(value = "#{param.dialogDataKey}")
  private String dialogDataKey;

  private Object value;
  private Map<String, String> requestParams;

  @PostConstruct
  public void init() {
    resolveDialogDataKey();
    resolveValue();
    resolveRequestParams();
  }

  public Object getValue() {
    if (value == null) {
      resolveValue();
    }
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public Map<String, String> getParams() {
    if (requestParams == null) {
      resolveRequestParams();
    }
    return requestParams == null ? Collections.emptyMap() : requestParams;
  }

  public String getDialogDataKey() {
    return dialogDataKey;
  }

  public void setDialogDataKey(String dialogDataKey) {
    this.dialogDataKey = dialogDataKey;
  }

  public DialogDataStore getDialogDataStore() {
    return dialogDataStore;
  }

  public void setDialogDataStore(DialogDataStore dialogDataStore) {
    this.dialogDataStore = dialogDataStore;
  }

  private void resolveDialogDataKey() {
    if (dialogDataKey != null && !dialogDataKey.trim().isEmpty()) {
      return;
    }
    FacesContext context = FacesContext.getCurrentInstance();
    if (context == null) {
      return;
    }
    Map<String, String> params = context.getExternalContext().getRequestParameterMap();
    dialogDataKey = params.get("dialogDataKey");
  }

  private void resolveValue() {
    if (dialogDataKey == null || dialogDataKey.trim().isEmpty()) {
      resolveDialogDataKey();
    }
    if (dialogDataKey == null || dialogDataKey.trim().isEmpty()) {
      return;
    }
    DialogDataStore store = dialogDataStore != null ? dialogDataStore : resolveDialogDataStore();
    if (store == null) {
      return;
    }
    value = store.get(dialogDataKey);
  }

  private void resolveRequestParams() {
    FacesContext context = FacesContext.getCurrentInstance();
    if (context == null) {
      return;
    }
    requestParams = context.getExternalContext().getRequestParameterMap();
  }

  private DialogDataStore resolveDialogDataStore() {
    FacesContext context = FacesContext.getCurrentInstance();
    if (context == null) {
      return null;
    }
    return context.getApplication().evaluateExpressionGet(context, "#{dialogDataStore}", DialogDataStore.class);
  }
}
