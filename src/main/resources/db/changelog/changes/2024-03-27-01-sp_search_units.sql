-- 2024-03-27-01-sp_search_units.sql
--liquibase formatted sql

--changeset mak.dz:1
--preconditions onFail:MARK_RAN
CREATE OR REPLACE FUNCTION search_units(
    p_start_date DATE,
    p_end_date DATE,
    p_user_id BIGINT,

    p_rooms_number INTEGER DEFAULT NULL,
    p_accommodation_type VARCHAR DEFAULT NULL,
    p_floor INTEGER DEFAULT NULL,
    p_sort_by VARCHAR DEFAULT 'created_at',
    p_sort_direction VARCHAR DEFAULT 'DESC',
    p_page_no INTEGER DEFAULT 1,
    p_page_size INTEGER DEFAULT 20
)
    RETURNS TABLE(
                     total_pages BIGINT,
                     is_available_for_user BOOLEAN,
                     total_count BIGINT,
                     row_num BIGINT,
                     users_booking_ids BIGINT[],
                     other_booking_ids BIGINT[],
                     id BIGINT,
                     rooms_number INTEGER,
                     accommodation_type accommodation_type,
                     floor INTEGER,
                     base_cost DECIMAL,
                     description TEXT,
                     is_available BOOLEAN,
                     created_at TIMESTAMPTZ,
                     updated_at TIMESTAMPTZ,
                     markup_percentage DECIMAL
                 )
    LANGUAGE plpgsql
AS $$
DECLARE
    v_offset INTEGER;
    v_limit INTEGER;
    v_where TEXT := 'WHERE 1=1';
    v_order_by TEXT;
    v_sort_direction TEXT;
    v_main_query TEXT;
BEGIN
    -- Input validation and sanitization
    IF p_page_no < 1 THEN p_page_no := 1; END IF;
    IF p_page_size < 1 THEN p_page_size := 20; END IF;

    -- Calculate offset and limit for pagination
    v_limit := p_page_size;
    v_offset := (p_page_no - 1) * p_page_size;

    -- Sanitize sort direction
    v_sort_direction := UPPER(p_sort_direction);
    IF v_sort_direction NOT IN ('ASC', 'DESC') THEN
        v_sort_direction := 'DESC';
    END IF;

    -- Validate and sanitize sort column
    CASE LOWER(p_sort_by)
        WHEN 'id' THEN v_order_by := 'u.id';
        WHEN 'name' THEN v_order_by := 'u.name';
        WHEN 'floor' THEN v_order_by := 'u.floor';
        WHEN 'rooms_number' THEN v_order_by := 'u.rooms_number';
        WHEN 'cost' THEN v_order_by := 'u.cost';
        WHEN 'created_at' THEN v_order_by := 'u.created_at';
        ELSE v_order_by := 'u.created_at';
        END CASE;

    -- Build the WHERE clause based on filter parameters
    IF p_rooms_number IS NOT NULL THEN
        v_where := v_where || ' AND u.rooms_number = ' || p_rooms_number;
    END IF;

    IF p_accommodation_type IS NOT NULL THEN
        v_where := v_where || ' AND u.accommodation_type = ''' || p_accommodation_type || '''';
    END IF;

    IF p_floor IS NOT NULL THEN
        v_where := v_where || ' AND u.floor = ' || p_floor;
    END IF;

    -- Build and execute the main query with window functions
    v_main_query := 'SELECT COUNT(*) OVER ()                               AS total_count,
                            ROW_NUMBER() OVER (ORDER BY u.created_at desc) AS row_num,
                            ARRAY_AGG(b.id) FILTER (WHERE b.status IS NOT NULL AND b.user_id =  ' || p_user_id || ' ) AS users_booking_ids,
                            ARRAY_AGG(b.id) FILTER (WHERE b.status IS NOT NULL AND b.user_id != ' || p_user_id || ')  AS other_booking_ids,
                            u.*
                       FROM units u
                       LEFT JOIN public.bookings b
                         on u.id = b.unit_id
                        and b.status IN (''PENDING'', ''CONFIRMED'')
                        and (b.start_date <= ''' || p_end_date || ''' AND b.end_date >= ''' || p_start_date || ''')
                      ' || v_where || '
                      GROUP BY u.id';

    -- Return the filtered subset based on calculated row numbers
    RETURN QUERY EXECUTE 'SELECT CEIL(wd.total_count::FLOAT / ' || p_page_size || ')::BIGINT AS total_pages,
                                 CASE
                                     WHEN wd.other_booking_ids IS NOT NULL AND wd.users_booking_ids IS NOT NULL THEN TRUE
                                     WHEN wd.other_booking_ids IS NOT NULL AND wd.users_booking_ids IS NULL THEN FALSE
                                     ELSE TRUE
                                     END AS is_available_for_user,
                                 wd.*
                          FROM (' || v_main_query || ') AS wd
                          WHERE wd.row_num > ' || v_offset || ' AND wd.row_num <= ' || (v_offset + v_limit) || '
                          ORDER BY wd.row_num';
END;
$$;
