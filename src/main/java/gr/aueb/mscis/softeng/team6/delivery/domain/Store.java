@Entity
@Table (name = "stores")

class Store implements Serializable {
  @Id
  @Column (name = "id")
  @GeneratedValue (strategy = GenerationType.AUTO)
  private int id;

  @Column (name = "name", nullable = false)
  private String name;

  @Column (name = "type", nullable = false)
  private String type;

  @OneToMany (mappedby = "store")
  private Set<Order> orders = new HashSet<> ();

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public Set<Order> getOrders() {
    return orders;
  }
}
