package com.cesar.InovaLab.InovaLab.controllers;

import com.cesar.InovaLab.InovaLab.models.Iniciativa;
import com.cesar.InovaLab.InovaLab.models.Solicitacao;
import com.cesar.InovaLab.InovaLab.models.StatusSolicitacao;

import com.cesar.InovaLab.InovaLab.models.UserProfessor;
import com.cesar.InovaLab.InovaLab.models.UserAluno;
import com.cesar.InovaLab.InovaLab.repository.IniciativaRepository;
import com.cesar.InovaLab.InovaLab.repository.SolicitacaoRepository;
import com.cesar.InovaLab.InovaLab.repository.UserAlunoRepository;
import com.cesar.InovaLab.InovaLab.repository.UserProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

import java.security.Principal;

@Controller
@RequestMapping("/home-professor")
public class IniciativaController {

    @Autowired
    private IniciativaRepository iniciativaRepository;

    @Autowired
    private UserAlunoRepository userAlunoRepository;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    // Método para carregar os dados da iniciativa na página de edição
    @GetMapping("/editar-iniciativa/{id}")
    public String editarIniciativa(@PathVariable("id") Long id, Model model) {
        // Aqui você busca a iniciativa com o ID fornecido
        Iniciativa iniciativa = iniciativaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Iniciativa inválida"));

        // Passa a iniciativa encontrada para a view de edição
        model.addAttribute("iniciativa", iniciativa);
        return "editar-iniciativa";
    }

    // Método para processar a edição da iniciativa
    @PostMapping("/editar-iniciativa/{id}")
    public String salvarIniciativaEditada(@PathVariable("id") Long id,
                                          @RequestParam("titulo") String titulo,
                                          @RequestParam("descricao") String descricao,
                                          RedirectAttributes redirectAttributes) {
        // Encontra a iniciativa existente
        Iniciativa iniciativa = iniciativaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Iniciativa inválida"));

        // Atualiza os dados da iniciativa
        iniciativa.setTitulo(titulo);
        iniciativa.setDescricao(descricao);

        // Salva as alterações
        iniciativaRepository.save(iniciativa);

        // Adiciona mensagem de sucesso
        redirectAttributes.addFlashAttribute("mensagem", "Iniciativa editada com sucesso!");
        return "redirect:/home-professor"; // Ou para a página desejada
    }

    @GetMapping("/iniciativa/{id}/solicitacoes")
    public String visualizarSolicitacoes(@PathVariable Long id, Model model) {
        Iniciativa iniciativa = iniciativaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Iniciativa inválida"));

        List<Solicitacao> solicitacoes = solicitacaoRepository.findByIniciativa(iniciativa);
        model.addAttribute("iniciativa", iniciativa);
        model.addAttribute("solicitacoes", solicitacoes);

        return "solicitacoes"; // Nome do template Thymeleaf
    }

    @PostMapping("/solicitacao/{id}/status")
    public String atualizarStatusSolicitacao(@PathVariable Long id,
                                             @RequestParam("status") StatusSolicitacao status,
                                             RedirectAttributes redirectAttributes) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));

        solicitacao.setStatus(status);
        solicitacaoRepository.save(solicitacao);

        redirectAttributes.addFlashAttribute("mensagem", "Solicitação atualizada com sucesso!");
        return "redirect:/home-professor/iniciativa/" + solicitacao.getIniciativa().getId() + "/solicitacoes";
    }
}
