package guru.furu.kgaBackend.adapter

import guru.furu.kgaBackend.client.dto.incoming.NewAccountDTO
import guru.furu.kgaBackend.client.dto.incoming.NewCommentDTO
import guru.furu.kgaBackend.client.dto.incoming.NewImageDTO
import guru.furu.kgaBackend.client.dto.outgoing.AccountDTO
import guru.furu.kgaBackend.client.dto.outgoing.CommentDTO
import guru.furu.kgaBackend.client.dto.outgoing.ImageDetailsDTO
import guru.furu.kgaBackend.client.dto.outgoing.ImageSummaryDTO
import guru.furu.kgaBackend.client.dto.outgoing.TagDTO
import guru.furu.kgaBackend.domain.nodes.Account
import guru.furu.kgaBackend.domain.nodes.Comment
import guru.furu.kgaBackend.domain.nodes.Tag
import guru.furu.kgaBackend.domain.nodes.incoming.NewAccount
import guru.furu.kgaBackend.domain.nodes.incoming.NewComment
import guru.furu.kgaBackend.domain.nodes.incoming.NewImage
import guru.furu.kgaBackend.domain.nodes.outgoing.ImageDetails
import guru.furu.kgaBackend.domain.nodes.outgoing.ImageSummary
import java.util.UUID

fun NewAccountDTO.toDomain(): NewAccount =
    NewAccount(
        userName = this.userName,
        email = this.email,
        password = this.password,
    )

fun Account.toAccountDTO(): AccountDTO =
    AccountDTO(
        id = this.nodeId.toString(),
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
        id = this.nodeId.toString(),
        authorId = this.authorId.toString(),
        onImageId = this.onImageId.toString(),
        text = this.text,
        date = this.date.epochSeconds,
    )

fun Tag.toDTO(): TagDTO =
    TagDTO(
        id = this.nodeId.toString(),
        title = this.title,
    )

fun ImageDetails.toDTO(): ImageDetailsDTO =
    ImageDetailsDTO(
        uploaderAccountId = this.uploaderAccountId.toString(),
        imageFilePath = this.imageFilePath,
        comments = this.comments.map { it.toDTO() },
        tags = this.tags.map { it.toDTO() },
        title = this.title,
        uploadedAt = this.uploadedAt.epochSeconds,
    )

fun ImageSummary.toDTO(): ImageSummaryDTO =
    ImageSummaryDTO(
        uploaderAccountId = this.uploaderAccountId.toString(),
        title = this.title,
        filePath = this.filePath,
        uploadedDate = this.uploadedDate.epochSeconds,
    )
