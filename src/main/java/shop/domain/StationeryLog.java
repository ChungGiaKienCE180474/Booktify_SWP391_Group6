package shop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "stationery_logs")
public class StationeryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stationery_id", nullable = true)
    private StationeryItem stationeryItem;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;

    // CREATE | UPDATE | DELETE
    @Column(nullable = false, length = 20)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    @Column(nullable = false)
    private LocalDateTime actionAt;

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        actionAt = LocalDateTime.now();
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StationeryItem getStationeryItem() { return stationeryItem; }
    public void setStationeryItem(StationeryItem stationeryItem) { this.stationeryItem = stationeryItem; }

    public User getStaff() { return staff; }
    public void setStaff(User staff) { this.staff = staff; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public LocalDateTime getActionAt() { return actionAt; }
    public void setActionAt(LocalDateTime actionAt) { this.actionAt = actionAt; }
}
