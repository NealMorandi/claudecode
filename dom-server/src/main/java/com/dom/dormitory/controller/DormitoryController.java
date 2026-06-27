package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.dto.DormitoryDTO;
import com.dom.dormitory.entity.vo.DormitoryVO;
import com.dom.dormitory.entity.vo.StorageVO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.entity.vo.RoomResidentVO;
import com.dom.dormitory.service.DormitoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 寮コントローラ */
@RestController
@RequestMapping("/dormitories")
@RequiredArgsConstructor
public class DormitoryController {

    private final DormitoryService dormitoryService;

    /** 一覧取得 */
    @GetMapping
    public Result<PageVO<DormitoryVO>> list(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String type,
            @RequestParam(required = false, defaultValue = "") String prefecture,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int pageSize) {
        return Result.success(dormitoryService.list(name, type, prefecture, status, page, pageSize), MessageConstant.GET_SUCCESS);
    }

    /** 詳細取得 */
    @GetMapping("/{id}")
    public Result<DormitoryVO> getById(@PathVariable Long id) {
        return Result.success(dormitoryService.getById(id), MessageConstant.GET_SUCCESS);
    }

    /** 新規登録 */
    @PostMapping
    public Result<DormitoryVO> create(@RequestBody DormitoryDTO dto) {
        return Result.success(dormitoryService.create(dto), MessageConstant.CREATE_SUCCESS);
    }

    /** 更新 */
    @PutMapping("/{id}")
    public Result<DormitoryVO> update(@PathVariable Long id, @RequestBody DormitoryDTO dto) {
        return Result.success(dormitoryService.update(id, dto), MessageConstant.UPDATE_SUCCESS);
    }

    /** 削除 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dormitoryService.delete(id);
        return Result.success(null, MessageConstant.DELETE_SUCCESS);
    }

    /** 部屋×在居者一覧（寮詳細画面用） */
    @GetMapping("/{id}/room-residents")
    public Result<List<RoomResidentVO>> getRoomResidents(@PathVariable Long id) {
        return Result.success(dormitoryService.getRoomResidents(id), MessageConstant.GET_SUCCESS);
    }

    /** 備品一覧（寮詳細画面用） */
    @GetMapping("/{id}/equipment")
    public Result<List<StorageVO>> getEquipment(@PathVariable Long id) {
        return Result.success(dormitoryService.getEquipmentByDormitory(id), MessageConstant.GET_SUCCESS);
    }
}
