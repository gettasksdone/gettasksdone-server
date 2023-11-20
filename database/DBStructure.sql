-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE usuario(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    email TEXT NOT NULL,
    password VARCHAR(255) NOT NULL COMMENT 'SHA'
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `info_usuario`
--

CREATE TABLE info_usuario(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    id_usuario_id int NOT NULL,
    nombre TEXT NOT NULL,
    telefono int NOT NULL,
    puesto TEXT NOT NULL,
    departamento TEXT NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `contexto`
--

CREATE TABLE contexto(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre TEXT NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `etiqueta`
--

CREATE TABLE etiqueta(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre TEXT NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nota`
--

CREATE TABLE nota(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    contenido TEXT NOT NULL,
    creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    proyecto_id int,
    tarea_id int
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `check_item`
--

CREATE TABLE check_item(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    tarea_id int NOT NULL,
    contenido TEXT NOT NULL,
    esta_marcado BOOL NOT NULL DEFAULT 0
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `proyecto`
--

CREATE TABLE proyecto(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre TEXT NOT NULL,
    inicio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fin DATETIME NOT NULL,
    descripcion TEXT NOT NULL,
    estado TEXT NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_proyecto`
--

CREATE TABLE usuario_proyecto(  
    id_usuario_id int NOT NULL,
    proyecto_id int NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tarea`
--

CREATE TABLE tarea(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    contexto_id int NOT NULL,
    descripcion TEXT NOT NULL,
    creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    vencimiento DATETIME,
    estado TEXT NOT NULL,
    prioridad int NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_tarea`
--

CREATE TABLE usuario_tarea(  
    id_usuario_id int NOT NULL,
    tarea_id int NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tarea_notas`
--

CREATE TABLE tarea_notas(  
    notas_id int NOT NULL,
    tarea_id int NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `proyecto_notas`
--

CREATE TABLE proyecto_notas(  
    notas_id int NOT NULL,
    proyecto_id int NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `etiqueta_tarea`
--

CREATE TABLE etiqueta_tarea(  
    id_etiqueta_id int NOT NULL,
    tarea_id int NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `proyecto_tareas`
--

CREATE TABLE proyecto_tareas(  
    proyecto_id int NOT NULL,
    tareas_id int NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `etiqueta_proyecto`
--

CREATE TABLE etiqueta_proyecto(  
    id_etiqueta_id int NOT NULL,
    proyecto_id int NOT NULL
);

--
-- Indices de la tabla `info_usuario`
--
ALTER TABLE `info_usuario`
  ADD KEY `usuario` (`id_usuario_id`);

--
-- Indices de la tabla `check_item`
--
ALTER TABLE `check_item`
  ADD KEY `tarea` (`tarea_id`);

--
-- Indices de la tabla `usuario_proyecto`
--
ALTER TABLE `usuario_proyecto`
  ADD KEY `usuario` (`id_usuario_id`),
  ADD KEY `proyecto` (`proyecto_id`);

--
-- Indices de la tabla `tarea`
--
ALTER TABLE `tarea`
  ADD KEY `contexto` (`contexto_id`);

--
-- Indices de la tabla `usuario_tarea`
--
ALTER TABLE `usuario_tarea`
  ADD KEY `usuario` (`id_usuario_id`),
  ADD KEY `tarea` (`tarea_id`);

--
-- Indices de la tabla `tarea_notas`
--
ALTER TABLE `tarea_notas`
  ADD KEY `nota` (`id_nota_id`),
  ADD KEY `tarea` (`tarea_id`);

--
-- Indices de la tabla `proyecto_notas`
--
ALTER TABLE `proyecto_notas`
  ADD KEY `nota` (`id_nota_id`),
  ADD KEY `proyecto` (`proyecto_id`);

--
-- Indices de la tabla `etiqueta_tarea`
--
ALTER TABLE `etiqueta_tarea`
  ADD KEY `etiqueta` (`id_etiqueta_id`),
  ADD KEY `tarea` (`tarea_id`);

--
-- Indices de la tabla `proyecto_tareas`
--
ALTER TABLE `proyecto_tareas`
  ADD KEY `proyecto` (`proyecto_id`),
  ADD KEY `tarea` (`tareas_id`);

--
-- Indices de la tabla `etiqueta_proyecto`
--
ALTER TABLE `etiqueta_proyecto`
  ADD KEY `etiqueta` (`id_etiqueta_id`),
  ADD KEY `proyecto` (`proyecto_id`);

--
-- Filtros para la tabla `info_usuario`
--
ALTER TABLE `info_usuario`
  ADD CONSTRAINT `InfoUsuario_fk1` FOREIGN KEY (`id_usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `check_item`
--
ALTER TABLE `check_item`
  ADD CONSTRAINT `CheckItem_fk` FOREIGN KEY (`tarea_id`) REFERENCES `tarea` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
  
--
-- Filtros para la tabla `usuario_proyecto`
--
ALTER TABLE `usuario_proyecto`
  ADD CONSTRAINT `UsuarioProyecto_fk1` FOREIGN KEY (`id_usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `UsuarioProyecto_fk2` FOREIGN KEY (`proyecto_id`) REFERENCES `proyecto` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
  
--
-- Filtros para la tabla `tarea`
--
ALTER TABLE `tarea`
  ADD CONSTRAINT `Tarea_fk2` FOREIGN KEY (`contexto_id`) REFERENCES `contexto` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
  
--
-- Filtros para la tabla `usuario_tarea`
--
ALTER TABLE `usuario_tarea`
  ADD CONSTRAINT `UsuarioTarea_fk1` FOREIGN KEY (`id_usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `UsuarioTarea_fk2` FOREIGN KEY (`tarea_id`) REFERENCES `tarea` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `tarea_notas`
--
ALTER TABLE `tarea_notas`
  ADD CONSTRAINT `NotaTarea_fk1` FOREIGN KEY (`id_nota_id`) REFERENCES `nota` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `NotaTarea_fk2` FOREIGN KEY (`tarea_id`) REFERENCES `tarea` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
      
--
-- Filtros para la tabla `proyecto_notas`
--
ALTER TABLE `proyecto_notas`
  ADD CONSTRAINT `NotaProyecto_fk1` FOREIGN KEY (`id_nota_id`) REFERENCES `nota` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `NotaProyecto_fk2` FOREIGN KEY (`proyecto_id`) REFERENCES `proyecto` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
      
--
-- Filtros para la tabla `etiqueta_tarea`
--
ALTER TABLE `etiqueta_tarea`
  ADD CONSTRAINT `EtiquetaTarea_fk1` FOREIGN KEY (`id_etiqueta_id`) REFERENCES `etiqueta` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `EtiquetaTarea_fk2` FOREIGN KEY (`tarea_id`) REFERENCES `tarea` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `etiqueta_tarea`
--
ALTER TABLE `proyecto_tareas`
  ADD CONSTRAINT `ProyectoTareas_fk1` FOREIGN KEY (`proyecto_id`) REFERENCES `proyecto` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `ProyectoTareas_fk2` FOREIGN KEY (`tareas_id`) REFERENCES `tarea` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `etiqueta_proyecto`
--
ALTER TABLE `etiqueta_proyecto`
  ADD CONSTRAINT `EtiquetaProyecto_fk1` FOREIGN KEY (`id_etiqueta_id`) REFERENCES `etiqueta` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `EtiquetaProyecto_fk2` FOREIGN KEY (`proyecto_id`) REFERENCES `proyecto` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

COMMIT;