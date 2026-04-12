package com.andi.carikopi.feature.dashboard.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    
    @JsonProperty("penjualan_hari_ini")
    private Map<String, Object> penjualanHariIni;
    
    @JsonProperty("jumlah_transaksi_hari_ini")
    private Map<String, Object> jumlahTransaksiHariIni;

    @JsonProperty("pendapatan_bulan_ini")
    private Map<String, Object> pendapatanBulanIni;

    @JsonProperty("menu_terlaris")
    private Map<String, Object> menuTerlaris;
    
    @JsonProperty("list_menu_terlaris")
    private List<Map<String, Object>> listMenuTerlaris;
    
    @JsonProperty("grafik_penjualan")
    private List<Map<String, Object>> grafikPenjualan;
    
}
