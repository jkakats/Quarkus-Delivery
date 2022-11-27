package gr.aueb.mscis.softeng.team6.delivery.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "stores")

class Store implements Serializable {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @Column (name = "name", nullable = false)
  private String name;

  @Column (name = "type", nullable = false)
  private String type;

  //TODO add Area field

  @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
  private Set<Order> orders = new HashSet<>();

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
