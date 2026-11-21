-- ==========================================================
-- Procedimientos almacenados (funciones PL/pgSQL)
-- Se usan para llenar los combos de la pantalla de Inscripción.
-- Ejecutar en Neon/DBeaver sobre la base neondb.
-- ==========================================================

-- Grupos para el combo: "G<id> · Materia · Docente · Aula · Horario"
CREATE OR REPLACE FUNCTION "practica-mvc".fn_listar_grupos_combo()
RETURNS TABLE (id_grupo INT, descripcion TEXT)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT g.id_grupo,
           'G' || g.id_grupo || ' · ' || m.nombre_materia || ' · ' || d.nombre
               || ' · ' || g.aula || ' · ' || g.horario
    FROM "practica-mvc".grupo g
    JOIN "practica-mvc".materia m ON m.id_materia = g.id_materia
    JOIN "practica-mvc".docente d ON d.id_docente = g.id_docente
    ORDER BY g.id_grupo;
END;
$$;

-- Estudiantes para el combo: "Nombre Apellido", ordenados por nombre
CREATE OR REPLACE FUNCTION "practica-mvc".fn_listar_estudiantes_combo()
RETURNS TABLE (id_estudiante INT, nombre_completo TEXT)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT e.id_estudiante,
           (e.nombre || ' ' || e.apellido)::TEXT
    FROM "practica-mvc".estudiante e
    ORDER BY e.nombre, e.apellido;
END;
$$;

-- Comprobación: deben salir los grupos y los estudiantes
SELECT * FROM "practica-mvc".fn_listar_grupos_combo();
SELECT * FROM "practica-mvc".fn_listar_estudiantes_combo();
