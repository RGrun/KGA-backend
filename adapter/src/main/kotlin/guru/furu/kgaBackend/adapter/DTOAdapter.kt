package guru.furu.kgaBackend.adapter

import guru.furu.kgaBackend.client.dto.incoming.NewAccountDTO
import guru.furu.kgaBackend.client.dto.incoming.NewCommentDTO
import guru.furu.kgaBackend.client.dto.incoming.NewImageDTO
import guru.furu.kgaBackend.client.dto.outgoing.AccountDTO
import guru.furu.kgaBackend.client.dto.outgoing.CommentDTO
import guru.furu.kgaBackend.client.dto.outgoing.TagDTO
import guru.furu.kgaBackend.domain.nodes.Account
import guru.furu.kgaBackend.domain.nodes.Comment
import guru.furu.kgaBackend.domain.nodes.Tag
import guru.furu.kgaBackend.domain.nodes.incoming.NewAccount
import guru.furu.kgaBackend.domain.nodes.incoming.NewComment
import guru.furu.kgaBackend.domain.nodes.incoming.NewImage
import java.util.UUID

fun NewAccountDTO.toDomain(): NewAccount =
    NewAccount(
        userName = this.userName,
        email = this.email,
        password = this.password,
    )

fun Account.toAccountDTO(): AccountDTO =
    AccountDTO(
        id = this.id.toString(),
        userName = this.userName,
        email = this.email,
        lastLogin = this.lastLogin,
    )

fun NewImageDTO.toDomain(): NewImage =
    NewImage(
        uploaderAccountId = UUID.fromString(this.uploaderAccountId),
        title = this.title,
        description = this.description,
    )

fun NewCommentDTO.toDomain(): NewComment =
    NewComment(
        authorId = UUID.fromString(this.authorId),
        onImageId = UUID.fromString(onImageId),
        text = this.text,
    )

fun Comment.toDTO(): CommentDTO =
    CommentDTO(
        id = this.id.toString(),
        authorId = this.authorId.toString(),
        onImageId = this.onImageId.toString(),
        text = this.text,
        date = this.date.epochSeconds,
    )

fun Tag.toDTO(): TagDTO =
    TagDTO(
        id = this.id.toString(),
        title = this.title,
    )
