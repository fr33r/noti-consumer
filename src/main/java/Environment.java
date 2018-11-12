import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Environment {

  private static final class Component<T> {

    private final String name;
    private final Class clazz;
    private final T instance;

    public Component(String name, Class clazz, T instance) {
      this.name = name;
      this.clazz = clazz;
      this.instance = instance;
    }

    public String getName() {
      return this.name;
    }

    public Class getClazz() {
      return this.clazz;
    }

    public T getInstance() {
      return this.instance;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null || obj.getClass() != this.getClass()) return false;
      if (obj == this) return true;

      Component component = (Component) obj;
      boolean sameName = this.getName().equals(component.getName());
      boolean sameClazz = this.getClazz() == component.getClazz();
      return sameName && sameClazz;
    }

    @Override
    public int hashCode() {
      final int prime = 17;
      int hashCode = 1;

      hashCode = hashCode * prime + this.getName().hashCode();
      hashCode = hashCode * prime + this.getClazz().hashCode();
      return hashCode;
    }
  }

  private Map<Class, List<Component>> componentMap;

  public Environment() {
    this.componentMap = new HashMap<>();
  }

  public <T> void register(String componentName, Class componentClass, T componentInstance) {
    Component<T> component = new Component<>(componentName, componentClass, componentInstance);
    if (!this.componentMap.containsKey(component.getClazz())) {
      List<Component> componentList = new ArrayList<>();
      componentList.add(component);
      this.componentMap.put(component.getClazz(), componentList);
      return;
    }

    if (this.componentMap.get(component.getClazz()).contains(component)) {
      return;
    }

    this.componentMap.get(component.getClazz()).add(component);
  }

  public <T> void register(Class componentClass, T componentInstance) {
    this.register(componentClass.getName(), componentClass, componentInstance);
  }

  public <T> T resolve(String componentName, Class componentClass) {
    if (!this.componentMap.containsKey(componentClass)) {
      return null;
    }

    Component componentPredicate = new Component(componentName, componentClass, null);
    for (Component component : this.componentMap.get(componentClass)) {
      if (component.equals(componentPredicate)) {
        return (T) component.getInstance();
      }
    }
    return null;
  }

  public <T> T resolve(Class componentClass) {
    return this.resolve(componentClass.getName(), componentClass);
  }
}
