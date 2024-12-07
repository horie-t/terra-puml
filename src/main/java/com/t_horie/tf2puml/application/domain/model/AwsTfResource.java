package com.t_horie.tf2puml.application.domain.model;

import lombok.Data;

import java.util.Optional;

/**
 * AWS tfファイルから読み込んだリソースを表すクラス
 */
@Data
public class AwsTfResource {
    /**
     * tfファイルでのリソースのタイプ
     */
    private String resourceType = "";
    /**
     * pumlでのリソースのエイリアス
     */
    private String alias = "";
    /**
     * pumlでのリソースのラベル
     */
    private String label = "";
    /**
     * pumlでのリソースのテクノロジー
     */
    private String tf2pumlTechnology = "";
    /**
     * pumlでのリソースの説明
     */
    private String description = "";
    /**
     * pumlでのリソースの親リソース
     */
    private Optional<String> parent = Optional.empty();
}
