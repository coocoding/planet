package com.planet.develop.Service;

import com.planet.develop.DTO.ExpenditureDTO;
import com.planet.develop.Entity.Expenditure;
import com.planet.develop.Entity.ExpenditureDetail;
import com.planet.develop.Entity.User;
import com.planet.develop.Enum.EcoEnum;
import com.planet.develop.Enum.money_Type;
import com.planet.develop.Enum.money_Way;
import com.planet.develop.Repository.ExpenditureDetailRepository;
import com.planet.develop.Repository.ExpenditureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ExpenditureDetailServiceImpl implements ExpenditureDetailService {

    private final ExpenditureRepository expenditureRepository;
    private final ExpenditureDetailRepository detailRepository;
    private final ExpenditureService service;

    private final EntityManager em;

    /** 지출 등록 */
    @Override
    public Long register(ExpenditureDTO dto) {
        ExpenditureDetail entity = dtoToEntity(dto);
        detailRepository.save(entity);
        return entity.getEno();
    }

    /** 하루 지출 총액 */
    @Override
    public String totalDay(User user, LocalDate date) {
        double total = 0;
        List<Object[]> exTypeList = expenditureRepository.getDayList(user, date);
        for (Object[] arr : exTypeList) {
            total += (double) arr[1];
        }
        return String.format("%.0f", total);
    }

    /** 하루 지출 유형별 총액 */
    @Override
    public String totalDayExType(User user, money_Type type, LocalDate date) {
        double total = 0;
        List<Object[]> exTypeList = expenditureRepository.getDayExTypeList(user, type, date);
        for (Object[] arr : exTypeList) {
            total += (double) arr[1];
        }
        return String.format("%.0f", total);
    }

    /** 하루 친반환경별 총액 */
    @Override
    public String totalDayEco(User user, EcoEnum eco, LocalDate date) {
        double total = 0;
        List<Object[]> ecoList = expenditureRepository.getDayEcoList(user, eco, date);
        for (Object[] arr : ecoList) {
            total += (double) arr[1];
        }
        return String.format("%.0f", total);
    }

    /** 하루 지출 방법별 총액 */
    @Override
    public String totalDayExWay(User user, money_Way way, LocalDate date) {
        double total = 0;
        List<Object[]> exWayList = expenditureRepository.getDayExWayList(user, way, date);
        for (Object[] arr : exWayList) {
            total += (double) arr[1];
        }
        return String.format("%.0f", total);
    }

    /** 한 달 지출 리스트 */
    @Override
    public List<Expenditure> findMonthExpenditure(User user, int month) {
        LocalDate startDate = LocalDate.of(2022,month,1);
        int lengthOfMonth = startDate.lengthOfMonth();
        LocalDate endDate = LocalDate.of(2022,month,lengthOfMonth);
        return em.createQuery("select e from Expenditure e left join ExpenditureDetail ed on e.eno = ed.eno " +
                "where :startDate <= e.date and e.date <= :endDate " +
                "and e.user = :user", Expenditure.class)
                .setParameter("startDate",startDate)
                .setParameter("endDate",endDate)
                .setParameter("user", user)
                .getResultList();
    }

    /** 한 달 지출 유형별 리스트 */
    @Override
    public List<Expenditure> findMonthExType(User user, int month, money_Type type) {
        LocalDate startDate = LocalDate.of(2022,month,1);
        int lengthOfMonth = startDate.lengthOfMonth();
        LocalDate endDate = LocalDate.of(2022,month,lengthOfMonth);
        return em.createQuery("select e from Expenditure e left join ExpenditureDetail ed on e.eno = ed.eno " +
                "where :startDate <= e.date and e.date <= :endDate " +
                "and e.user = :user and e.detail.exType = :type", Expenditure.class)
                .setParameter("startDate",startDate)
                .setParameter("endDate",endDate)
                .setParameter("user", user)
                .setParameter("type", type)
                .getResultList();
    }

    /** 한 달 지출 방법별 리스트 */
    @Override
    public List<Expenditure> findMonthExWay(User user, int month, money_Way way) {
        LocalDate startDate = LocalDate.of(2022,month,1);
        int lengthOfMonth = startDate.lengthOfMonth();
        LocalDate endDate = LocalDate.of(2022,month,lengthOfMonth);
        return em.createQuery("select e from Expenditure e left join ExpenditureDetail ed on e.eno = ed.eno " +
                "where :startDate <= e.date and e.date <= :endDate " +
                "and e.user = :user and e.detail.exWay = :way", Expenditure.class)
                .setParameter("startDate",startDate)
                .setParameter("endDate",endDate)
                .setParameter("user", user)
                .setParameter("way", way)
                .getResultList();
    }

    /** 한 달 친반환경별 리스트 */
    @Override
    public List<Expenditure> findMonthEco(User user, int month, EcoEnum eco) {
        LocalDate startDate = LocalDate.of(2022,month,1);
        int lengthOfMonth = startDate.lengthOfMonth();
        LocalDate endDate = LocalDate.of(2022,month,lengthOfMonth);
        return em.createQuery("select e from Expenditure e left join ExpenditureDetail ed on e.eno = ed.eno " +
                "where :startDate <= e.date and e.date <= :endDate " +
                "and e.user = :user and e.detail.eco = :eco", Expenditure.class)
                .setParameter("startDate",startDate)
                .setParameter("endDate",endDate)
                .setParameter("user", user)
                .setParameter("eco", eco)
                .getResultList();
    }

    /** 한 달 지출 총액 */
    @Override
    public String totalMonth(User user, int month) {
        double total = 0;
        List<Expenditure> exList = findMonthExpenditure(user, month);
        for (Expenditure e : exList) {
            ExpenditureDTO dto = service.entityToDto(e);
            total += dto.getCost();
        }
        return String.format("%.0f", total);
    }

    /** 한 달 지출 유형별 총액 */
    @Override
    public String totalMonthExType(User user, int month, money_Type type) {
        double total = 0;
        List<Expenditure> exTypeList = findMonthExType(user, month, type);
        for (Expenditure e : exTypeList) {
            ExpenditureDTO dto = service.entityToDto(e);
            total += dto.getCost();
        }
        return String.format("%.0f", total);
    }

    /** 한 달 지출 방법별 총액 */
    @Override
    public String totalMonthExWay(User user, int month, money_Way way) {
        double total = 0;
        List<Expenditure> exWayList = findMonthExWay(user, month, way);
        for (Expenditure e : exWayList) {
            ExpenditureDTO dto = service.entityToDto(e);
            total += dto.getCost();
        }
        return String.format("%.0f", total);
    }



    /** 한 달 친반환경별 지출 총액 */
    @Override
    public String totalMonthEco(User user, int month, EcoEnum eco) {
        double total = 0;
        List<Expenditure> ecoList = findMonthEco(user, month, eco);
        for (Expenditure e : ecoList) {
            ExpenditureDTO dto = service.entityToDto(e);
            total += dto.getCost();
        }
        return String.format("%.0f", total);
    }



}
