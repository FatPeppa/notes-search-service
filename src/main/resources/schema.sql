CREATE SCHEMA IF NOT EXISTS public;

CREATE TABLE IF NOT EXISTS public.notes (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    name text NOT NULL,
    note_category_id bigint,
    note_category_name varchar,
    text_extraction text,
    media_id uuid,
    created_date timestamp with time zone,
    last_change_date timestamp with time zone,
    note_content text,
    tsvector_name tsvector,
    tsvector_content tsvector,
    tsvector_name_content tsvector,
    CONSTRAINT pk_notes PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.tags (
    id bigint NOT NULL,
    note_id bigint NOT NULL,
    tag_id bigint NOT NULL,
    tag_name varchar NOT NULL,
    CONSTRAINT pk_tags PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.media_ids (
    id bigint NOT NULL,
    note_id bigint NOT NULL,
    media_id uuid NOT NULL,
    CONSTRAINT pk_media_ids PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.normalized_note_search_info (
    id bigint NOT NULL,
    note_id bigint NOT NULL,
    user_id bigint NOT NULL,
    normalized_name text NOT NULL,
    normalized_note_content text,
    tsvector_name tsvector,
    tsvector_content tsvector,
    tsvector_name_content tsvector,
    CONSTRAINT pk_normalized_note_search_info PRIMARY KEY (id)
);

ALTER TABLE public.tags
    DROP CONSTRAINT IF EXISTS "fk_tags_notes";

ALTER TABLE public.media_ids
    DROP CONSTRAINT IF EXISTS "fk_media_ids_notes";

ALTER TABLE public.normalized_note_search_info
    DROP CONSTRAINT IF EXISTS "fk_normalized_note_search_info_notes";

ALTER TABLE public.tags
    ADD CONSTRAINT "fk_tags_notes"
    FOREIGN KEY (note_id)
    REFERENCES public.notes (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
;

ALTER TABLE public.media_ids
    ADD CONSTRAINT "fk_media_ids_notes"
    FOREIGN KEY (note_id)
    REFERENCES public.notes (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
;

ALTER TABLE public.normalized_note_search_info
    ADD CONSTRAINT "fk_normalized_note_search_info_notes"
    FOREIGN KEY (note_id)
    REFERENCES public.notes (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
;

CREATE INDEX IF NOT EXISTS "ixfk_media_ids_notes"
    ON public.media_ids
    USING btree(note_id ASC)
;

CREATE INDEX IF NOT EXISTS "ix_media_ids_media_id"
    ON public.media_ids
    USING btree(media_id ASC)
;

CREATE INDEX IF NOT EXISTS "ix_tags_note_id_tag_id"
    ON public.tags
    USING btree(note_id ASC, tag_id ASC)
;

CREATE INDEX IF NOT EXISTS idx_note_name
    ON public.notes
    USING btree(name);

CREATE INDEX IF NOT EXISTS idx_note_content
    ON public.notes
    USING btree(note_content);

CREATE INDEX IF NOT EXISTS "ixfk_normalized_note_search_info_notes"
    ON public.normalized_note_search_info
    USING btree(note_id ASC)
;

CREATE INDEX IF NOT EXISTS "ix_normalized_note_search_info_user_id_note_id"
    ON public.normalized_note_search_info
    USING btree(user_id ASC, note_id)
;

CREATE INDEX IF NOT EXISTS idx_fulltext_note_name
    ON public.notes
    USING GIN(tsvector_name);

CREATE INDEX IF NOT EXISTS idx_fulltext_note_content
    ON public.notes
    USING GIN(tsvector_content)
;

CREATE INDEX IF NOT EXISTS idx_fulltext_note_content
    ON public.notes
    USING GIN(tsvector_name_content)
;

CREATE INDEX IF NOT EXISTS idx_fulltext_normalized_note_search_info_note_name
    ON public.normalized_note_search_info
    USING GIN(tsvector_name);

CREATE INDEX IF NOT EXISTS idx_fulltext_normalized_note_search_info_note_content
    ON public.normalized_note_search_info
    USING GIN(tsvector_content)
;

CREATE INDEX IF NOT EXISTS idx_fulltext_normalized_note_search_info_note_content
    ON public.normalized_note_search_info
    USING GIN(tsvector_name_content)
;

--TO DO: Добавить триггеры на создание/изменение/удаление данных о заметке с переносом сведений в табличку нормализации с применением
    --функции UPPERCASE
--CREATE OR REPLACE FUNCTION


CREATE SEQUENCE IF NOT EXISTS public.media_ids_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE
;

CREATE SEQUENCE IF NOT EXISTS public.tags_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE
;

CREATE SEQUENCE IF NOT EXISTS public.normalized_note_search_info_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE
;

DROP TEXT SEARCH CONFIGURATION IF EXISTS public.ru;

DROP TEXT SEARCH DICTIONARY IF EXISTS public.russian_ispell;

CREATE TEXT SEARCH DICTIONARY public.russian_ispell (
    TEMPLATE = ispell,
    DictFile = russian,
    AffFile = russian,
    StopWords = russian
);

CREATE TEXT SEARCH CONFIGURATION public.ru (
    COPY = pg_catalog.russian
);

-- Alter configuration to use russian_ispell dictionary.
ALTER TEXT SEARCH CONFIGURATION public.ru
    ALTER MAPPING FOR hword, hword_part, word with public.russian_ispell, russian_stem;

CREATE OR REPLACE FUNCTION public.trigger_note_insert()
    RETURNS TRIGGER
    LANGUAGE plpgsql AS '
BEGIN
    INSERT INTO public.normalized_note_search_info
        VALUES (
                   nextval(''public.normalized_note_search_info_seq''),
                   NEW.id,
                   NEW.user_id,
                   upper(NEW.name),
                   upper(NEW.note_content),
                   to_tsvector(''public.ru'',upper(NEW.name)),
                   to_tsvector(''public.ru'',upper(coalesce(NEW.note_content, ''''))),
                   to_tsvector(''public.ru'',upper(NEW.name)||'' ''||coalesce(upper(NEW.note_content),''''))
               );
    RETURN NEW;
END;'
;


CREATE OR REPLACE FUNCTION public.trigger_note_update()
    RETURNS TRIGGER
    LANGUAGE plpgsql AS '
BEGIN
    UPDATE
        public.normalized_note_search_info
    SET
        normalized_name = upper(new.name),
        normalized_note_content = upper(new.note_content),
        tsvector_name = to_tsvector(''public.ru'',upper(new.name)),
        tsvector_content = to_tsvector(''public.ru'',upper(coalesce(new.note_content, ''''))),
        tsvector_name_content = to_tsvector(''public.ru'',upper(new.name)||'' ''||coalesce(upper(new.note_content),''''))
    WHERE note_id = new.id;

    RETURN NULL;
END;'
;

CREATE OR REPLACE TRIGGER trigger_copy_notes_to_normalized_note_search_info
    AFTER INSERT ON public.notes
    FOR EACH ROW
    EXECUTE PROCEDURE public.trigger_note_insert();

CREATE OR REPLACE TRIGGER trigger_update_notes_to_normalized_note_search_info
    AFTER UPDATE
    ON public.notes
    FOR EACH ROW
    EXECUTE PROCEDURE public.trigger_note_update();