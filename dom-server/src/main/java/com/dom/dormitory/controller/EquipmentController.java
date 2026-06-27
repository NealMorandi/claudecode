package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.dto.EquipmentDTO;
import com.dom.dormitory.entity.dto.ResidenceItemDTO;
import com.dom.dormitory.entity.dto.StorageRegisterDTO;
import com.dom.dormitory.entity.vo.EquipmentHistoryVO;
import com.dom.dormitory.entity.vo.EquipmentVO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.entity.vo.ResidenceItemVO;
import com.dom.dormitory.entity.vo.StorageVO;
import com.dom.dormitory.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** 備品コントローラ */
@RestController
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    /** 備品一覧 */
    @GetMapping("/equipment")
    public Result<PageVO<EquipmentVO>> list(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String category,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int pageSize) {
        return Result.success(equipmentService.list(name, category, page, pageSize), MessageConstant.GET_SUCCESS);
    }

    /** 備品登録 */
    @PostMapping("/equipment")
    public Result<EquipmentVO> create(@RequestBody EquipmentDTO dto) {
        return Result.success(equipmentService.create(dto), MessageConstant.CREATE_SUCCESS);
    }

    /** 備品更新 */
    @PutMapping("/equipment/{id}")
    public Result<EquipmentVO> update(@PathVariable Long id, @RequestBody EquipmentDTO dto) {
        return Result.success(equipmentService.update(id, dto), MessageConstant.UPDATE_SUCCESS);
    }

    /** 備品削除 */
    @DeleteMapping("/equipment/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return Result.success(null, MessageConstant.DELETE_SUCCESS);
    }

    /** 倉庫備品一覧 */
    @GetMapping("/equipment/storage")
    public Result<List<StorageVO>> storage(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String location) {
        return Result.success(equipmentService.listStorage(name, location), MessageConstant.GET_SUCCESS);
    }

    /** 備品保管登録 */
    @PostMapping("/equipment/{id}/storage")
    public Result<Void> registerStorage(@PathVariable Long id, @RequestBody StorageRegisterDTO dto) {
        equipmentService.registerStorage(id, dto);
        return Result.success(null, MessageConstant.CREATE_SUCCESS);
    }

    /** 全備品履歴一覧（備品ID省略可） */
    @GetMapping("/equipment/history")
    public Result<PageVO<EquipmentHistoryVO>> listAllHistory(
            @RequestParam(required = false)                       Long   equipmentId,
            @RequestParam(required = false, defaultValue = "")   String action,
            @RequestParam(required = false, defaultValue = "1")  int    page,
            @RequestParam(required = false, defaultValue = "20") int    pageSize) {
        return Result.success(equipmentService.listHistory(equipmentId, action, page, pageSize), MessageConstant.GET_SUCCESS);
    }

    /** 備品履歴一覧（備品ID指定） */
    @GetMapping("/equipment/{id}/history")
    public Result<PageVO<EquipmentHistoryVO>> listHistory(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "") String action,
            @RequestParam(required = false, defaultValue = "1")  int page,
            @RequestParam(required = false, defaultValue = "20") int pageSize) {
        return Result.success(equipmentService.listHistory(id, action, page, pageSize), MessageConstant.GET_SUCCESS);
    }

    /** 入居備品一覧取得 */
    @GetMapping("/residences/{residenceId}/items")
    public Result<List<ResidenceItemVO>> listItems(@PathVariable Long residenceId) {
        return Result.success(equipmentService.listByResidence(residenceId), MessageConstant.GET_SUCCESS);
    }

    /** 入居備品更新 */
    @PutMapping("/residences/{residenceId}/items")
    public Result<List<ResidenceItemVO>> updateItems(@PathVariable Long residenceId,
                                                      @RequestBody List<ResidenceItemDTO> items) {
        return Result.success(equipmentService.updateItems(residenceId, items), MessageConstant.UPDATE_SUCCESS);
    }

    /** 保管一覧の保管場所更新（equipment_history） */
    @PutMapping("/equipment/history/{id}/storage-location")
    public Result<Void> updateHistoryStorageLocation(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        equipmentService.updateHistoryStorageLocation(id, body.get("storageLocation"));
        return Result.success(null, MessageConstant.UPDATE_SUCCESS);
    }

    /** 備品明細更新（equipment_history 全フィールド） */
    @PutMapping("/equipment/history/{id}")
    public Result<Void> updateHistory(@PathVariable Long id, @RequestBody StorageRegisterDTO dto) {
        equipmentService.updateHistory(id, dto);
        return Result.success(null, MessageConstant.UPDATE_SUCCESS);
    }
}
