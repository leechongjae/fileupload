package com.ohgiraffers.mybatistojpa.menu.service;

import com.ohgiraffers.mybatistojpa.menu.dto.CategoryDTO;
import com.ohgiraffers.mybatistojpa.menu.dto.MenuDTO;
import com.ohgiraffers.mybatistojpa.menu.entity.Category;
import com.ohgiraffers.mybatistojpa.menu.entity.Menu;
import com.ohgiraffers.mybatistojpa.menu.repository.CategoryRepository;
import com.ohgiraffers.mybatistojpa.menu.repository.MenuRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final ModelMapper modelMapper;
    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public MenuService(ModelMapper modelMapper, MenuRepository menuRepository, CategoryRepository categoryRepository) {
        this.menuRepository = menuRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    /* 목차. 1. findById() */
    public MenuDTO findMenuByCode(int menuCode) {

        /* 설명. findById 메소드는 이미 부모 인터페이스(CrudRepository)에 구현되어 있으므로 인터페이스에 따로 정의할
        *   필요가 없다. findById의 반환값은 Optional 타입이다. 이 Optional 타입은 NPE을 방지하기 위한 다양한 기능이
        *   존재한다. 해당 id로 조회를 실패할 경우 IllegalArgumentException 예외를 발생시킨다. */

        Menu menu = menuRepository.findById(menuCode)
                .orElseThrow(IllegalArgumentException::new);

        /* 설명. modelMapper를 이용하여 entity를 DTO로 변환해서 컨트롤러로 반환 */
        return modelMapper.map(menu, MenuDTO.class);
    }

    /* 목차. 2. findAll() -> 페이징 처리 전 */
    public List<MenuDTO> findMenuList() {

        List<Menu> menuList = menuRepository.findAll(Sort.by("menuCode").descending());

        return menuList.stream().map(menu -> modelMapper.map(menu, MenuDTO.class)).collect(Collectors.toList());
    }

    /* 목차. 3. findAll() -> 페이징 처리 후 */
    public Page<MenuDTO> findMenuList(Pageable pageable) {

        /* 설명. page 파라미터가 Pageable의 number 값으로 넘어오는데 해당 값이 조회시에는 인덱스 기준이 되어야 해서
        *   -1 처리가 필요하다. */
        pageable = PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() -1,
                pageable.getPageSize(),
                Sort.by("menuCode").descending());

        Page<Menu> menuList = menuRepository.findAll(pageable);

        return menuList.map(menu -> modelMapper.map(menu, MenuDTO.class));

    }

    /* 목차. 4. QueryMethod */
    public List<MenuDTO> findByMenuPrice(Integer menuPrice) {

        List<Menu> menuList = menuRepository.findByMenuPriceGreaterThan(menuPrice, Sort.by("menuPrice").descending());

        List<MenuDTO> menuDTOList = new ArrayList<>();

        for (Menu menu : menuList){
            MenuDTO menuDTO = modelMapper.map(menu, MenuDTO.class);
            menuDTOList.add(menuDTO);
        }

        return menuDTOList;
    }

    /* 목차. 5. JPQL or Native Query*/
    public List<CategoryDTO> findAllCategory() {

        /* 설명. findAll() 메소드를 사용해 전체 카테고리를 조회하는 것이 당연히 가능하다.
        *   하지만, 이 예제에서는 SpringDataJPA에서 JPQL이나 Native Query를 사용할 때 어떻게 설정하고 사용해야 하는지를 중점으로 본다.
        *   따라서, CategoryRepository에 Native Query를 사용해 직접 메소들를 정의한다. */
        List<Category> categoryList = categoryRepository.findAll();

        return categoryList.stream().map(category -> modelMapper.map(category, CategoryDTO.class)).collect(Collectors.toList());

    }

    /* 목차. 6 save - 엔티티 저장 */
    @Transactional
    public void registMenu(MenuDTO menuDTO){

        Menu menuEntity = modelMapper.map(menuDTO, Menu.class);

        menuRepository.save(menuEntity);
//        menuRepository.save(modelMapper.map(menuDTO, Menu.class));

    }

    @Transactional
    public void modifyMenu(MenuDTO modifyMenu) {

        Menu menuToModify = menuRepository.findById(modifyMenu.getMenuCode())
                                            .orElseThrow(IllegalArgumentException::new);
        menuToModify.setMenuName(modifyMenu.getMenuName());
        menuToModify.setMenuPrice(modifyMenu.getMenuPrice());
        menuToModify.setCategoryCode(modifyMenu.getCategoryCode());
        menuToModify.setOrderableStatus(modifyMenu.getOrderableStatus());
    }

    /* 목차. 8. delete - deleteById(CrudRepository) */
    @Transactional
    public void deleteMenu(Integer menuCode) {
        menuRepository.deleteById(menuCode);
    }

    public List<MenuDTO> findAllMenu() {

        List<Menu> menuList = menuRepository.findAll(Sort.by("menuCode").descending());

        return menuList.stream().map(menu -> modelMapper.map(menu, MenuDTO.class)).collect(Collectors.toList());

    }
}
