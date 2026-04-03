package beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;

@ManagedBean(name = "dialogResultBean")
@ViewScoped
public class DialogResultBean implements Serializable {
  private static final long serialVersionUID = 1L;

  public void close(Object result) {
    PrimeFaces.current().dialog().closeDynamic(result);
  }

  public void cancel() {
    PrimeFaces.current().dialog().closeDynamic(null);
  }
}
