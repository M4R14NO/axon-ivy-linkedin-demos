package beans;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "dialogDataStore")
@SessionScoped
public class DialogDataStore implements Serializable {
  private static final long serialVersionUID = 1L;

  private final Map<String, Object> store = new ConcurrentHashMap<>();

  public String put(Object value) {
    String key = UUID.randomUUID().toString();
    store.put(key, value);
    return key;
  }

  public void put(String key, Object value) {
    store.put(key, value);
  }

  public Object get(String key) {
    return store.get(key);
  }

  public Object remove(String key) {
    return store.remove(key);
  }

  public Map<String, Object> getStore() {
    return store;
  }
}