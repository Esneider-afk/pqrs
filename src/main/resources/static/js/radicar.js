(function () {
    const tipo = document.getElementById('tipo');
    const asunto = document.getElementById('asunto');
    const descripcion = document.getElementById('descripcion');
    const anonimo = document.getElementById('anonimo');
    const bloqueAnonimo = document.getElementById('bloqueAnonimo');
    const datosPersonales = document.getElementById('datosPersonales');
    const plazo = document.getElementById('plazo');
    const contador = document.getElementById('contador');
    const caja = document.getElementById('sugerencia');
    const btnUsar = document.getElementById('usarSugerencia');
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    let temporizador = null;
    let ultimoTexto = null;
    let sugerido = null;

    function actualizarTipo() {
        const op = tipo.options[tipo.selectedIndex];
        plazo.textContent = op && op.dataset.plazo ? op.dataset.plazo + ' días hábiles' : '—';
        const esDenuncia = tipo.value === 'DENUNCIA';
        bloqueAnonimo.classList.toggle('d-none', !esDenuncia);
        if (!esDenuncia) anonimo.checked = false;
        actualizarAnonimo();
    }

    function actualizarAnonimo() {
        datosPersonales.classList.toggle('d-none', anonimo.checked);
    }

    function actualizarContador() {
        contador.textContent = descripcion.value.length + ' / 4000';
    }

    async function sugerir() {
        const texto = (asunto.value + ' ' + descripcion.value).trim();
        if (texto.length < 15) { caja.classList.add('d-none'); return; }
        if (texto === ultimoTexto) return;
        ultimoTexto = texto;
        try {
            const r = await fetch('/api/clasificar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', [csrfHeader]: csrfToken },
                body: JSON.stringify({ texto })
            });
            if (!r.ok) return;
            sugerido = await r.json();
            document.getElementById('sugTipo').textContent = sugerido.tipoEtiqueta + ' (' + sugerido.plazo + ' d. háb.)';
            document.getElementById('sugDependencia').textContent = sugerido.dependencia;
            document.getElementById('sugPrioridad').textContent = sugerido.prioridad;
            caja.classList.remove('d-none');
        } catch (e) { /* la sugerencia es opcional; se ignora cualquier error de red */ }
    }

    function programarSugerencia() {
        clearTimeout(temporizador);
        temporizador = setTimeout(sugerir, 600);
    }

    tipo.addEventListener('change', actualizarTipo);
    anonimo.addEventListener('change', actualizarAnonimo);
    descripcion.addEventListener('input', () => { actualizarContador(); programarSugerencia(); });
    asunto.addEventListener('input', programarSugerencia);
    btnUsar.addEventListener('click', () => {
        if (sugerido) { tipo.value = sugerido.tipo; actualizarTipo(); }
    });

    actualizarTipo();
    actualizarContador();
    sugerir();
})();