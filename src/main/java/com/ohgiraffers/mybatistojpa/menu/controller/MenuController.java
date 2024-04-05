package com.ohgiraffers.mybatistojpa.menu.controller;

import com.ohgiraffers.mybatistojpa.menu.dto.CategoryDTO;
import com.ohgiraffers.mybatistojpa.menu.dto.MenuDTO;
import com.ohgiraffers.mybatistojpa.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private ResourceLoader resourceLoader;

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("regist")
    public void regist(){}

    /* 설명. /menu/regist.html 문서가 열리자 마자 JS코드의 fetch() 메소드를 통해 "/menu/category" 비동기 요청이 들어온다. */
    @GetMapping(value = "category", produces = "application/json; charset=UTF-8")
    /* 설명. @ResponsBody
     *   - 메소드에 @ResponsBody가 붙은 메소드의 반환형은 View Resolver가 해석하지 않는다.
     *   - 핸들러 메소드의 반환형은 Don't Care : 어차피 요청이 들어온 곳으로 모두 JSON 문자열로 처리되어 반환된다.
     *   - 한글이 포함된 데이터는 produces 속성에 "application/json"라는 MIME 타입과 "charset=UTF-8" 인코딩 타입을 설정해줘야 한다. */
    @ResponseBody
    public List<CategoryDTO> findCategoryList(){


        List<CategoryDTO> foundCategorys = menuService.findAllCategory();

        System.out.println("foundCategorys = " + foundCategorys);
        return foundCategorys;
    }

    @PostMapping("/regist")
    public String registNewMenu(MenuDTO menu,
                                Model model,
                                @RequestParam MultipartFile singleFile,
                                RedirectAttributes rAttr)
            throws IOException {

        System.out.println("menu = " + menu);

        /* 설명. 파일 업로드 */
        Resource resource = resourceLoader.getResource("classpath:static/uploadedFiles/img/single");
        String filePath = null;

        if(!resource.exists()){
            String root = "src/main/resources/static/uploadedFiles/img/single";
            File file = new File(root);
            file.mkdirs();

            filePath = file.getAbsolutePath();
        }else{
            filePath = resourceLoader.getResource("classpath:static/uploadedFiles/img/single")
                    .getFile()
                    .getAbsolutePath();
        }

        /* 설명. 실제 파일이 저장될 경로를 확인.*/
        System.out.println("빌드된 single 디렉토리의 절대 경로 = " + filePath);

        /* 설명. 파일명 변경 처리*/
        String originalFileName = singleFile.getOriginalFilename();
        System.out.println("원래 파일 이름 = " + originalFileName);

        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        System.out.println("파일 확장자 = " + extension);

        String savedName = UUID.randomUUID().toString().replace("-", "");

        System.out.println("저장될 파일 이름 = " + savedName);


        try{ // 로직이 실행되는 블럭

            /* 설명. 실제로 파일이 저장되는 명령어.*/
            singleFile.transferTo(new File(filePath + "/" + savedName + extension));

            /* 설명. 실제로 이 부분에서 DB를 다녀오는 Service 계층(비즈니스 로직)이 작성되는 위치다. */
            // DO SOMTHING...

            rAttr.addFlashAttribute("message", "[SUCCESS] 단일 파일 업로드 성공!");

            System.out.println("[IntelliJ] [SUCCESS] 단일 파일 업로드 성공!");
        }catch (IOException e){ // try문에서 예외가 발생했을 때 해당 블럭으로 이동됨(즉 예외를 처리하는 영역)

            /* 설명. try 구문 안, 즉 비즈니스 로직 처리 도중 예외가 발생했다면 아마 파일만 업로드 되어 있을 것이다.
             *   그 파일은 주인이 없는 (missing) 파일이 되어 쓰레기처럼 서버에 쌓일 것이기 때문에 이를 삭제해줘야 한다.
             * */
            new File(filePath + "/" + savedName).delete();

            e.printStackTrace();

            rAttr.addFlashAttribute("message", "[SUCCESS] 단일 파일 업로드 실패!");

            System.out.println("[IntelliJ] [FAILED] 단일 파일 업로드 실패!");
        }

        menu.setMenuPictureName(savedName);
        menu.setMenuPictureExtension(extension);

        menu.setImg("static/uploadedFiles/img/single" + "/" + savedName + extension);

        menuService.registMenu(menu);

        List<MenuDTO> menuList1 = menuService.findMenuList();

        model.addAttribute(menuList1);

        return "redirect:/menu/list";
    }



    @GetMapping("list")
    public String list(Model model){

        List<MenuDTO> menuList = menuService.findAllMenu();

        for (MenuDTO menuDTO : menuList){
            menuDTO.setImg("static/uploadedFiles/img/single/" + menuDTO.getMenuPictureName() + menuDTO.getMenuPictureExtension());
        }

        model.addAttribute("menuList", menuList);

        return "menu/list";
    }

    @GetMapping("/detail/{code}")
    public String findMenuDetail(@PathVariable("code") int code,
                                 Model model){

        // DO SOMETHING
        MenuDTO menu = menuService.findMenuByCode(code);

        model.addAttribute("menu", menu);

        return "menu/detail";
    }

    @GetMapping("/edit/{code}")
    public String modifyMenu(@PathVariable("code") int code,
                                 Model model){

        // DO SOMETHING
        MenuDTO menu = menuService.findMenuByCode(code);

        model.addAttribute("menu", menu);

        return "menu/edit";
    }

    @GetMapping("edit")
    public String editPage(@RequestParam("codeNum") int codeNum){
        return "redirect:/menu/edit/" + codeNum;
    }

    @GetMapping("modify")
    public void mofifyPage(){}

    @PostMapping("modify")
    public String modifyMenu(MenuDTO menuModify){

        menuService.modifyMenu(menuModify);

        return "redirect:/menu/detail/" + menuModify.getMenuCode();
    }






    @GetMapping("/delete")
    public void deleteMenuPage(){}

    @PostMapping("delete")
    public String deleteMenu(@RequestParam Integer menuCode){

        menuService.deleteMenu(menuCode);

        return "redirect:/menu/list";

    }

    @PostMapping("delete/{code}")
    public String deleteMenudt(@PathVariable int code){

        menuService.deleteMenu(code);

        return "redirect:/menu/list";

    }



}
