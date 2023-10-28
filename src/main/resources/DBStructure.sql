-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Usuario`
--

CREATE TABLE Usuario(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    email TEXT NOT NULL,
    password VARCHAR(255) NOT NULL COMMENT 'SHA'
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `InfoUsuario`
--

CREATE TABLE InfoUsuario(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    idUsuario int NOT NULL,
    superior int NOT NULL,
    nombre TEXT NOT NULL,
    telefono int NOT NULL,
    puesto TEXT NOT NULL,
    departamento TEXT NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Contexto`
--

CREATE TABLE Contexto(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre TEXT NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Etiqueta`
--

CREATE TABLE Etiqueta(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre TEXT NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Nota`
--

CREATE TABLE Nota(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    contenido TEXT NOT NULL,
    fechaCreacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `CheckItem`
--

CREATE TABLE CheckItem(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    idTarea int NOT NULL,
    contenido TEXT NOT NULL,
    estaMarcado BOOL NOT NULL DEFAULT 0
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Proyecto`
--

CREATE TABLE Proyecto(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    nombre TEXT NOT NULL,
    fechaInicio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fechaFin DATETIME NOT NULL,
    descripcion TEXT NOT NULL,
    estado TEXT NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `UsuarioProyecto`
--

CREATE TABLE UsuarioProyecto(  
    idUsuario int NOT NULL,
    idProyecto int NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Tarea`
--

CREATE TABLE Tarea(  
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    idProyecto int NOT NULL,
    idContexto int NOT NULL,
    descripcion TEXT NOT NULL,
    fechaCreacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fechaVencimiento DATETIME,
    estado TEXT NOT NULL,
    prioridad int NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `UsuarioTarea`
--

CREATE TABLE UsuarioTarea(  
    idUsuario int NOT NULL,
    idTarea int NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `NotaTarea`
--

CREATE TABLE NotaTarea(  
    idNota int NOT NULL,
    idTarea int NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `NotaProyecto`
--

CREATE TABLE NotaProyecto(  
    idNota int NOT NULL,
    idProyecto int NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `EtiquetaTarea`
--

CREATE TABLE EtiquetaTarea(  
    idEtiqueta int NOT NULL,
    idTarea int NOT NULL
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `EtiquetaProyecto`
--

CREATE TABLE EtiquetaProyecto(  
    idEtiqueta int NOT NULL,
    idProyecto int NOT NULL
);

--
-- Indices de la tabla `InfoUsuario`
--
ALTER TABLE `InfoUsuario`
  ADD KEY `Usuario` (`idUsuario`),
  ADD KEY `Superior` (`superior`);

--
-- Indices de la tabla `CheckItem`
--
ALTER TABLE `CheckItem`
  ADD KEY `Tarea` (`idTarea`);

--
-- Indices de la tabla `UsuarioProyecto`
--
ALTER TABLE `UsuarioProyecto`
  ADD KEY `Usuario` (`idUsuario`),
  ADD KEY `Proyecto` (`idProyecto`);

--
-- Indices de la tabla `Tarea`
--
ALTER TABLE `Tarea`
  ADD KEY `Proyecto` (`idProyecto`),
  ADD KEY `Contexto` (`idContexto`);

--
-- Indices de la tabla `UsuarioTarea`
--
ALTER TABLE `UsuarioTarea`
  ADD KEY `Usuario` (`idUsuario`),
  ADD KEY `Tarea` (`idTarea`);

--
-- Indices de la tabla `NotaTarea`
--
ALTER TABLE `NotaTarea`
  ADD KEY `Nota` (`idNota`),
  ADD KEY `Tarea` (`idTarea`);

--
-- Indices de la tabla `NotaProyecto`
--
ALTER TABLE `NotaProyecto`
  ADD KEY `Nota` (`idNota`),
  ADD KEY `Proyecto` (`idProyecto`);

--
-- Indices de la tabla `EtiquetaTarea`
--
ALTER TABLE `EtiquetaTarea`
  ADD KEY `Etiqueta` (`idEtiqueta`),
  ADD KEY `Tarea` (`idTarea`);

--
-- Indices de la tabla `EtiquetaProyecto`
--
ALTER TABLE `EtiquetaProyecto`
  ADD KEY `Etiqueta` (`idEtiqueta`),
  ADD KEY `Proyecto` (`idProyecto`);

--
-- Filtros para la tabla `InfoUsuario`
--
ALTER TABLE `InfoUsuario`
  ADD CONSTRAINT `InfoUsuario_fk1` FOREIGN KEY (`idUsuario`) REFERENCES `Usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `InfoUsuario_fk2` FOREIGN KEY (`superior`) REFERENCES `Usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `CheckItem`
--
ALTER TABLE `CheckItem`
  ADD CONSTRAINT `CheckItem_fk` FOREIGN KEY (`idTarea`) REFERENCES `Tarea` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
  
--
-- Filtros para la tabla `UsuarioProyecto`
--
ALTER TABLE `UsuarioProyecto`
  ADD CONSTRAINT `UsuarioProyecto_fk1` FOREIGN KEY (`idUsuario`) REFERENCES `Usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `UsuarioProyecto_fk2` FOREIGN KEY (`idProyecto`) REFERENCES `Proyecto` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
  
--
-- Filtros para la tabla `Tarea`
--
ALTER TABLE `Tarea`
  ADD CONSTRAINT `Tarea_fk1` FOREIGN KEY (`idProyecto`) REFERENCES `Proyecto` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Tarea_fk2` FOREIGN KEY (`idContexto`) REFERENCES `Contexto` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
  
--
-- Filtros para la tabla `UsuarioTarea`
--
ALTER TABLE `UsuarioTarea`
  ADD CONSTRAINT `UsuarioTarea_fk1` FOREIGN KEY (`idUsuario`) REFERENCES `Usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `UsuarioTarea_fk2` FOREIGN KEY (`idTarea`) REFERENCES `Tarea` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
    
--
-- Filtros para la tabla `NotaTarea`
--
ALTER TABLE `NotaTarea`
  ADD CONSTRAINT `NotaTarea_fk1` FOREIGN KEY (`idNota`) REFERENCES `Nota` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `NotaTarea_fk2` FOREIGN KEY (`idTarea`) REFERENCES `Tarea` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
      
--
-- Filtros para la tabla `NotaProyecto`
--
ALTER TABLE `NotaProyecto`
  ADD CONSTRAINT `NotaProyecto_fk1` FOREIGN KEY (`idNota`) REFERENCES `Nota` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `NotaProyecto_fk2` FOREIGN KEY (`idProyecto`) REFERENCES `Proyecto` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
      
--
-- Filtros para la tabla `EtiquetaTarea`
--
ALTER TABLE `EtiquetaTarea`
  ADD CONSTRAINT `EtiquetaTarea_fk1` FOREIGN KEY (`idEtiqueta`) REFERENCES `Etiqueta` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `EtiquetaTarea_fk2` FOREIGN KEY (`idTarea`) REFERENCES `Tarea` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
      
--
-- Filtros para la tabla `EtiquetaProyecto`
--
ALTER TABLE `EtiquetaProyecto`
  ADD CONSTRAINT `EtiquetaProyecto_fk1` FOREIGN KEY (`idEtiqueta`) REFERENCES `Etiqueta` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `EtiquetaProyecto_fk2` FOREIGN KEY (`idProyecto`) REFERENCES `Proyecto` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

COMMIT;