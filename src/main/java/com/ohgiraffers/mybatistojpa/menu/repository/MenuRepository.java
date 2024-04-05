package com.ohgiraffers.mybatistojpa.menu.repository;

import com.ohgiraffers.mybatistojpa.menu.entity.Menu;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MenuRepository extends JpaRepository<Menu, Integer> {

    /* 설명. JpaRepository를 상속 받아서 사용하는 메소드 외의 메소드는 직접 정의해야 한다. */

    /* 목차. 1. 전달 받은 가격을 초과하는 메뉴의 목록을 조회하는 메소드 */
//    List<Menu> findMenuByMenuPriceGreaterThan(Integer menuPrice);
    List<Menu> findByMenuPriceGreaterThan(Integer menuPrice);

    /*목차. 2. 전달 받은 가격을 초과하는 메뉴의 목록을 가격 순으로 조회하는 메소드 */
    List<Menu> findByMenuPriceGreaterThanOrderByMenuPrice(Integer menuPrice);

    /* 목차. 3. 전달 받은 가격을 초과하는 메뉴의 목록을 전달 받는 정렬 기준으로 조회하는 메소드 */
    List<Menu> findByMenuPriceGreaterThan(Integer menuPrice, Sort sort);


}
