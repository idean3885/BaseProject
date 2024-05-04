INSERT INTO MEMBER
(id, mdfcn_dt, mdfcn_id, reg_dt, reg_id, detail_address, email, name, password, road_name_address, phone_no, role_list,
 use_yn)
VALUES (1, now(), 1, now(), 1, '상세 주소', 'admin@email.com', 'admin',
        '$2a$12$4zpqtjxUOdkFOi3ZK6j10uZMCxFgBDapvau6X.JUZa6etPT/qYqOC', '도로명 주소', '01234567890', 'ADMIN', 'Y');