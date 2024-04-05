-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE usuario(  
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    rol int NOT NULL,
    password VARCHAR(255) NOT NULL COMMENT 'SHA'
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `info_usuario`
--

CREATE TABLE info_usuario(  
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    usuario_id bigint NOT NULL,
    nombre TEXT NOT NULL,
    telefono BIGINT NOT NULL,
    puesto TEXT NOT NULL,
    departamento TEXT NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `contexto`
--

CREATE TABLE contexto(  
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre TEXT NOT NULL,
    usuario_id bigint NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `etiqueta`
--

CREATE TABLE etiqueta(  
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre TEXT NOT NULL,
    usuario_id bigint NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `nota`
--

CREATE TABLE nota(  
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    contenido TEXT NOT NULL,
    creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    usuario_id bigint NOT NULL,
    proyecto_id bigint,
    tarea_id bigint
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `check_item`
--

CREATE TABLE check_item(  
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    contenido TEXT NOT NULL,
    esta_marcado BOOL NOT NULL DEFAULT 0,
    usuario_id bigint NOT NULL,
    tarea_id bigint NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `proyecto`
--

CREATE TABLE proyecto(  
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre TEXT NOT NULL,
    inicio DATETIME DEFAULT CURRENT_TIMESTAMP,
    fin DATETIME NOT NULL,
    descripcion TEXT NOT NULL,
    estado TEXT NOT NULL,
    usuario_id bigint NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tarea`
--

CREATE TABLE tarea(  
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    contexto_id bigint NOT NULL,
    descripcion TEXT NOT NULL,
    creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    vencimiento DATETIME,
    estado TEXT NOT NULL,
    prioridad int NOT NULL,
    usuario_id bigint NOT NULL,
    proyecto_id bigint NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tarea_notas`
--

CREATE TABLE tarea_notas(  
    notas_id bigint NOT NULL,
    tarea_id bigint NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `proyecto_notas`
--

CREATE TABLE proyecto_notas(  
    notas_id bigint NOT NULL,
    proyecto_id bigint NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `etiqueta_tarea`
--

CREATE TABLE etiqueta_tarea(  
    id_etiqueta_id bigint NOT NULL,
    tarea_id bigint NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `proyecto_tareas`
--

CREATE TABLE proyecto_tareas(  
    proyecto_id bigint NOT NULL,
    tareas_id bigint NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `etiqueta_proyecto`
--

CREATE TABLE etiqueta_proyecto(  
    id_etiqueta_id bigint NOT NULL,
    proyecto_id bigint NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `etiqueta_proyecto`
--

CREATE TABLE tarea_check_items(  
    check_items_id bigint NOT NULL,
    tarea_id bigint NOT NULL
);

ALTER TABLE `usuario`
  ADD UNIQUE (username, email);

--
-- Indices de la tabla `tarea_check_items`
--
ALTER TABLE `tarea_check_items`
  ADD KEY `check_items` (`check_items_id`),
  ADD KEY `tarea` (`tarea_id`);

--
-- Indices de la tabla `contexto`
--
ALTER TABLE `contexto`
  ADD KEY `usuario` (`usuario_id`);

--
-- Indices de la tabla `etiqueta`
--
ALTER TABLE `etiqueta`
  ADD KEY `usuario` (`usuario_id`);

--
-- Indices de la tabla `nota`
--
ALTER TABLE `nota`
  ADD KEY `usuario` (`usuario_id`),
  ADD KEY `proyecto` (`proyecto_id`),
  ADD KEY `tarea` (`tarea_id`);

--
-- Indices de la tabla `check_item`
--
ALTER TABLE `check_item`
  ADD KEY `usuario` (`usuario_id`),
  ADD KEY `tarea` (`tarea_id`);

--
-- Indices de la tabla `info_usuario`
--
ALTER TABLE `info_usuario`
  ADD KEY `usuario` (`usuario_id`);

--
-- Indices de la tabla `proyecto`
--
ALTER TABLE `proyecto`
  ADD KEY `usuario` (`usuario_id`);

--
-- Indices de la tabla `tarea`
--
ALTER TABLE `tarea`
  ADD KEY `usuario` (`usuario_id`),
  ADD KEY `proyecto` (`proyecto_id`),
  ADD KEY `contexto` (`contexto_id`);

--
-- Indices de la tabla `tarea_notas`
--
ALTER TABLE `tarea_notas`
  ADD KEY `nota` (`notas_id`),
  ADD KEY `tarea` (`tarea_id`);

--
-- Indices de la tabla `proyecto_notas`
--
ALTER TABLE `proyecto_notas`
  ADD KEY `nota` (`notas_id`),
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
-- Filtros para la tabla `tarea_check_items`
--
ALTER TABLE `tarea_check_items`
  ADD CONSTRAINT `TareaCheckItems_fk1` FOREIGN KEY (`check_items_id`) REFERENCES `check_item` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `TareaCheckItems_fk2` FOREIGN KEY (`tarea_id`) REFERENCES `tarea` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;


--
-- Filtros para la tabla `info_usuario`
--
ALTER TABLE `info_usuario`
  ADD CONSTRAINT `InfoUsuario_fk1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
 
--
-- Filtros para la tabla `proyecto`
--
ALTER TABLE `proyecto`
  ADD CONSTRAINT `Proyecto_fk1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
 
--
-- Filtros para la tabla `contexto`
--
ALTER TABLE `contexto`
  ADD CONSTRAINT `Contexto_fk1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `etiqueta`
--
ALTER TABLE `etiqueta`
  ADD CONSTRAINT `Etiqueta_fk1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `nota`
--
ALTER TABLE `nota`
  ADD CONSTRAINT `Nota_fk1` FOREIGN KEY (`proyecto_id`) REFERENCES `proyecto` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Nota_fk2` FOREIGN KEY (`tarea_id`) REFERENCES `tarea` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Nota_fk3` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `check_item`
--
ALTER TABLE `check_item`
  ADD CONSTRAINT `CheckItem_fk1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `CheckItem_fk2` FOREIGN KEY (`tarea_id`) REFERENCES `tarea` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `tarea`
--
ALTER TABLE `tarea`
  ADD CONSTRAINT `Tarea_fk1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Tarea_fk2` FOREIGN KEY (`proyecto_id`) REFERENCES `proyecto` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Tarea_fk3` FOREIGN KEY (`contexto_id`) REFERENCES `contexto` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `tarea_notas`
--
ALTER TABLE `tarea_notas`
  ADD CONSTRAINT `NotaTarea_fk1` FOREIGN KEY (`notas_id`) REFERENCES `nota` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `NotaTarea_fk2` FOREIGN KEY (`tarea_id`) REFERENCES `tarea` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
      
--
-- Filtros para la tabla `proyecto_notas`
--
ALTER TABLE `proyecto_notas`
  ADD CONSTRAINT `NotaProyecto_fk1` FOREIGN KEY (`notas_id`) REFERENCES `nota` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
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