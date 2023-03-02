package com.dykim.base.hello.v1.entity;

import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class HelloBaseEntity {

    @Comment("hello 등록자 ID")
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long createdId;

    @Comment("hello 등록시간, yyyyMMddHHmmSSS")
    @CreatedDate
    @Column(nullable = false, length = 23, updatable = false)
    private LocalDateTime createdDateTime;

    @Comment("hello 수정자 ID")
    @LastModifiedBy
    @Column(nullable = false, updatable = false)
    private Long updatedId;

    @Comment("hello 수정시간, yyyyMMddHHmmSSS")
    @LastModifiedDate
    @Column(nullable = false, length = 23)
    private LocalDateTime updatedDateTime;

}
