/*Area is only table instead of being both embedded and table like in domain model,
 because it's not technically possible to be both at the same time.
 */

package gr.aueb.mscis.softeng.team6.delivery.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "areas")
class Area implements Serializable {
  @Id
  @Column (name = "zipcode")
  private int zipCode;

  @Column (name = "city")
  private String city;

  @Column (name = "state")
  private String state;

  @ManyToOne (fetch = FetchType.LAZY)
  private Store store_area;

  @OneToOne(mappedBy = "area_c", fetch = FetchType.LAZY)
  private Client client_a;

  public int getZipCode() {
    return zipCode;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public Store getStore_area() {
    return store_area;
  }

  public Client getClient_a() {
    return client_a;
  }

  boolean ValidateZipCode () {
    if (zipCode >= 10000 && zipCode <= 99999) return true;
    return false;
  }
}










