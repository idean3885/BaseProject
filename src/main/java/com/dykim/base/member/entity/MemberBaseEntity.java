package com.dykim.base.member.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class MemberBaseEntity {
    @Comment("등록ID")
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long regId;

    @Comment("등록일시, yyyyMMddHHmmSSS")
    @CreatedDate
    @Column(nullable = false, updatable = false, length = 23)
    private LocalDateTime regDt;

    @Comment("수정ID")
    @LastModifiedBy
    @Column(nullable = false, updatable = false)
    private Long mdfcnId;

    @Comment("수정일시, yyyyMMddHHmmSSS")
    @LastModifiedDate
    @Column(nullable = false, length = 23)
    private LocalDateTime mdfcnDt;
}
