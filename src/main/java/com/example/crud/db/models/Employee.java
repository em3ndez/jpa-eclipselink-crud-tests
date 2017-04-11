package com.example.crud.db.models;

import com.example.crud.util.SerialVersionUID;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.persistence.annotations.*;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import javax.persistence.CollectionTable;
import javax.persistence.Convert;
import javax.persistence.Index;
import javax.swing.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static javax.persistence.TemporalType.DATE;
import static org.eclipse.persistence.annotations.CacheCoordinationType.SEND_NEW_OBJECTS_WITH_CHANGES;
import static org.eclipse.persistence.annotations.DatabaseChangeNotificationType.INVALIDATE;
import static org.eclipse.persistence.config.CacheIsolationType.ISOLATED;

@EqualsAndHashCode(callSuper=true)
@EnableJpaAuditing
@EntityListeners(AuditingEntityListener.class)
@SecondaryTable(name = "SALARY")
@NamedQueries({
        @NamedQuery(name = "Employee.findAll", query = "SELECT e FROM Employee e ORDER BY e.id"),
        @NamedQuery(name = "Employee.findByName", query = "SELECT e FROM Employee e WHERE e.firstName LIKE :firstName AND e.lastName LIKE :lastName"),
        @NamedQuery(name = "Employee.count", query = "SELECT COUNT(e) FROM Employee e"),
        @NamedQuery(name = "Employee.countByName", query = "SELECT COUNT(e) FROM Employee e WHERE e.firstName LIKE :firstName AND e.lastName LIKE :lastName"),
        // Query used in {@link IdInPaging}
        @NamedQuery(name = "Employee.idsIn", query = "SELECT e FROM Employee e WHERE e.id IN :IDS ORDER BY e.id",
                hints = { @QueryHint(name = QueryHints.QUERY_RESULTS_CACHE, value = HintValues.TRUE) }) })
@Cache(type = CacheType.SOFT_WEAK
      ,expiryTimeOfDay=@TimeOfDay(hour=1)
      ,disableHits=false
      ,isolation=ISOLATED
      ,alwaysRefresh=true
      ,refreshOnlyIfNewer=true
      ,databaseChangeNotificationType=INVALIDATE
      ,coordinationType=SEND_NEW_OBJECTS_WITH_CHANGES)
@Table(name = "EMPLOYEE",
       indexes={
        @Index(name="EMP_SSN_INDEX", unique=true, columnList="SSN"),
        @Index(name="EMP_EMAIL_INDEX", columnList="EMAIL"),
        @Index(name="EMP_F_NAME_INDEX", columnList="FIRST_NAME"),
        @Index(name="EMP_L_NAME_INDEX", columnList="LAST_NAME"),
        @Index(name="EMP_NAME_INDEX", columnList="FIRST_NAME LAST_NAME") })
public @Data @Entity class Employee extends AbstractModel<Long> {
    private static final long serialVersionUID = SerialVersionUID.compute(AbstractModel.class);

    // PRIMARY KEY
    @Id @Column @GeneratedValue(generator = "flake-seq")
    private Long id;

    // FIELDS
    @Column(name="SSN") @CacheIndex @NotNull private String socialSecurityNumber;
    @Column private String honorific;
    @Column(name="FIRST_NAME") private String firstName;
    @Column(name="LAST_NAME") private String lastName;
    @Column private String suffix;
    @Column(name="EMAIL") private String emailAddress;
    @Column @Past @Temporal(DATE) private Calendar birthdate;
    @Column @Digits(integer=3, fraction=0) @NotNull @Min(0) @Max(125) private int age = 0;
    @Basic(fetch=FetchType.LAZY) @Lob private ImageIcon picture;

    /* NOTE: Gender mapped using Basic with an ObjectTypeConverter to map between
     * single char code value in database to enum. JPA only supports mapping to
     * the full name of the enum or its ordinal value. */
    @Basic @Column(name = "GENDER") @Convert(converter = GenderConverter.class) private Gender gender = Gender.Male;

    @Column(table = "SALARY")
    private double salary;

    // RELATIONS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_ID")
    @JsonBackReference
    private Employee manager;

    @OneToMany(mappedBy = "manager")
    private List<Employee> managedEmployees = new ArrayList<Employee>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrivateOwned
    @JsonManagedReference
    private List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "startDate", column = @Column(name = "START_DATE")),
            @AttributeOverride(name = "endDate", column = @Column(name = "END_DATE")) })
    private EmploymentPeriod period;

    @ElementCollection
    @CollectionTable(name = "RESPONS")
    private List<String> responsibilities = new ArrayList<String>();


    public Employee addManagedEmployee(Employee employee) {
        getManagedEmployees().add(employee);
        employee.setManager(this);
        return employee;
    }

    public Employee removeManagedEmployee(Employee employee) {
        getManagedEmployees().remove(employee);
        employee.setManager(null);
        return employee;
    }

    public PhoneNumber addPhoneNumber(PhoneNumber phoneNumber) {
        getPhoneNumbers().add(phoneNumber);
        phoneNumber.setOwner(this);
        return phoneNumber;
    }

    public PhoneNumber addPhoneNumber(String type, String number) {
        PhoneNumber phoneNumber = new PhoneNumber(type, number);
        return addPhoneNumber(phoneNumber);
    }

    public PhoneNumber removePhoneNumber(PhoneNumber phoneNumber) {
        getPhoneNumbers().remove(phoneNumber);
        phoneNumber.setOwner(null);
        return phoneNumber;
    }

    public void addResponsibility(String responsibility) {
        getResponsibilities().add(responsibility);
    }

    public void removeResponsibility(String responsibility) {
        getResponsibilities().remove(responsibility);
    }

}
