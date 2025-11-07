(function () {
  function removeHeroMessages() {
    document.querySelectorAll('.hero-message').forEach((element) => {
      element.remove();
    });
  }

  function initHeaderWatcher() {
    const header = document.querySelector('header');
    if (!header) {
      return;
    }

    let lastScrollY = window.scrollY;
    let ticking = false;
    const revealThreshold = header.offsetHeight;

    const update = () => {
      const currentScroll = window.scrollY;

      if (currentScroll > lastScrollY && currentScroll > revealThreshold) {
        header.classList.add('header-hidden');
      } else if (currentScroll < lastScrollY - 4 || currentScroll <= revealThreshold) {
        header.classList.remove('header-hidden');
      }

      lastScrollY = currentScroll;
      ticking = false;
    };

    window.addEventListener(
      'scroll',
      () => {
        if (!ticking) {
          window.requestAnimationFrame(update);
          ticking = true;
        }
      },
      { passive: true }
    );
  }

  function init() {
    removeHeroMessages();
    initHeaderWatcher();
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init, { once: true });
  } else {
    init();
  }
})();
