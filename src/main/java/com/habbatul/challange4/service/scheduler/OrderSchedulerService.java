package com.habbatul.challange4.service.scheduler;

import com.habbatul.challange4.enums.OrderStatus;
import com.habbatul.challange4.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrderSchedulerService {
    @Autowired
    OrderRepository orderRepository;


    //format anotasi : second minute hour day month day-of-week year
    //akan menghapus semua order dengan status COMPLETED tiap 1 menit

//    @Scheduled(cron = "0 */1 * * * *")

    //akan menghapus semua order dengan status COMPLETED tiap 6 jam
    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void deleteCompleteOrder() {
        log.info("Penghapusan berjalan");
        orderRepository.deleteAllByCompleted(OrderStatus.COMPLETE);
    }

    //blueprint bila butuh scheduler tiap waktu makan siang 13.00
    @Scheduled(cron = "0 0 13 * * *")
    @Transactional
    public void remindLunch() {
        log.info("Promo 1");
    }

    //blueprint bila butuh scheduler tiap waktu makan malam/ jam 20.00
    @Scheduled(cron = "0 0 20 * * *")
    @Transactional
    public void remindDinner() {
        log.info("Promo 2");
    }

}
