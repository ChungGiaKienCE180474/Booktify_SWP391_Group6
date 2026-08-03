package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.domain.StationeryLog;

import java.util.List;

@Repository
public interface StationeryLogRepository extends JpaRepository<StationeryLog, Long> {

    // Lấy toàn bộ log của một item, sắp xếp mới nhất trước
    List<StationeryLog> findByStationeryItemIdOrderByActionAtDesc(Long stationeryId);
}
